package eu.mihosoft.ugshell.vugshell.util;

import eu.mihosoft.ugshell.vugshell.Shell;
import eu.mihosoft.ugshell.ugdist.UGDist;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ShellImpl implements Shell {

    private static File executableFile;
    private static File ugRootPath;
    private final Process ugshellprocess;
    private static boolean initialized;

    static {
        // static init
    }
    private final File wd;

    private ShellImpl(Process proc, File wd) {
        this.ugshellprocess = proc;
        this.wd = wd;
    }

    /**
     * Initializes property folder and executable.
     */
    private static void initialize() {

        // already initialized: we don't do anything
        if (initialized) {
            return;
        }

        try {
            Path confDir
                    = Paths.get(System.getProperty("user.home"), ".ugshell").
                            toAbsolutePath();
            Path distDir = Paths.get(confDir.toString(), "ug-dist");
            File base = confDir.toFile();

            if (!Files.exists(confDir)) {
                Files.createDirectory(confDir);
            }

            if (!Files.exists(distDir)) {
                Files.createDirectory(distDir);
            }

            ConfigurationFile confFile
                    = IOUtil.newConfigurationFile(new File(base, "config.xml"));
            confFile.load();
            String timestamp = confFile.getProperty("timestamp");
            File ugFolder = new File(distDir.toFile(), "ug");

            if (timestamp == null || !ugFolder.exists()) {
                System.out.println(
                        " -> installing ug to \"" + distDir + "\"");
                UGDist.extractTo(distDir.toFile());
                String currentTimestamp = "" + ugFolder.lastModified();
                confFile.setProperty("timestamp", currentTimestamp);
                confFile.save();
            } else {
                String currentTimestamp = "" + ugFolder.lastModified();
                if (!Objects.equals(timestamp, currentTimestamp)) {
                    System.out.println(
                            " -> updating ug in \"" + distDir + "\"");
                    UGDist.extractTo(distDir.toFile());
                    currentTimestamp = "" + ugFolder.lastModified();
                    confFile.setProperty("timestamp", currentTimestamp);
                    confFile.save();
                } else {
                    System.out.println(
                            " -> ug up to date in \"" + distDir + "\"");
                }
            }

            executableFile = getExecutablePath(distDir);

        } catch (IOException ex) {
            Logger.getLogger(ShellImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        initialized = true;
    }

    @Override
    public ShellImpl print(PrintStream out, PrintStream err) {
        new StreamGobbler(err, ugshellprocess.getErrorStream(), "").start();
        new StreamGobbler(out, ugshellprocess.getInputStream(), "").start();

        return this;
    }

    @Override
    public ShellImpl print() {
        new StreamGobbler(System.err, ugshellprocess.getErrorStream(), "")
                .start();
        new StreamGobbler(System.out, ugshellprocess.getInputStream(), "")
                .start();

        return this;
    }

    @Override
    public ShellImpl waitFor() {
        try {
            ugshellprocess.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(ShellImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot wait until process is finished", ex);
        }

        return this;
    }

    /**
     * Executes ugshell with the specified script.
     *
     * @param wd working directory
     * @param script script that shall be executed
     * @return this shell
     */
    public static ShellImpl execute(File wd, String script) {
        File tmpDir;
        File scriptFile;
        try {
            tmpDir = Files.createTempDirectory("ugshell-script-tmp").toFile();
            scriptFile = new File(tmpDir, "code.lua");
            Files.write(scriptFile.toPath(), script.getBytes("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(ShellImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot execute script due to io exception", ex);
        }

        return execute(tmpDir, scriptFile);
    }

    /**
     * Executes ugshell with the specified script.
     *
     * @param wd working directory
     * @param script script that shall be executed
     * @return this shell
     */
    public static ShellImpl execute(File wd, File script) {

        initialize();

        Path scriptFile = null;

        try {
            scriptFile = Files.createTempFile("ugshell_script", ".lua");

            String scriptCode = new String(
                    Files.readAllBytes(script.toPath()), "UTF-8");

            scriptCode = "ug_set_root_path(\""
                    + ugRootPath.getAbsolutePath() + "\")\n"
                    + scriptCode;

            Files.write(scriptFile,
                    scriptCode.getBytes(Charset.forName("UTF-8")));

        } catch (IOException ex) {
            Logger.getLogger(ShellImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Cannot create tmp script-file", ex);
        }

        Process proc = execute(
                false, wd, "-ex",
                scriptFile.toAbsolutePath().toString());

        return new ShellImpl(proc, wd);
    }

    @Override
    public File getWorkingDirectory() {
        return wd;
    }

    /**
     * Calls ugshell with the specified arguments.
     *
     * @param arguments arguments
     * @param wd working directory
     * @param waitFor indicates whether to wait for process execution
     * @return ugshell process
     */
    public static Process execute(boolean waitFor, File wd, String... arguments) {

        initialize();

        if (arguments == null || arguments.length == 0) {
            arguments = new String[]{"--help"};
        }

        String[] cmd = new String[arguments.length + 1];

        cmd[0] = executableFile.getAbsolutePath();

        for (int i = 1; i < cmd.length; i++) {
            cmd[i] = arguments[i - 1];
        }

        Process proc = null;

        try {
            proc = Runtime.getRuntime().exec(cmd, null, wd);
            if (waitFor) {
                proc.waitFor();
            }
        } catch (IOException | InterruptedException ex) {
            throw new RuntimeException("Error while executing ugshell", ex);
        }

        return proc;
    }

    @Override
    public Process getProcess() {
        return ugshellprocess;
    }

    /**
     * Destroys the currently running ugshell process.
     */
    @Override
    public void destroy() {
        if (ugshellprocess != null) {
            ugshellprocess.destroy();
        }
    }

    /**
     * Returns the path to the ugshell executable. If the executable has not
     * been initialized this will be done as well.
     *
     * @return the path to the ugshell executable
     */
    private static File getExecutablePath(Path dir) {

        if (!VSysUtil.isOsSupported()) {
            throw new UnsupportedOperationException(
                    "The current OS is not supported: "
                    + System.getProperty("os.name"));
        }

        if (executableFile == null || !executableFile.isFile()) {

            ugRootPath = new File(dir.toFile(), "ug");

            String executableName = "bin/ugshell";

            if (VSysUtil.isWindows()) {
                executableName += ".exe";
            }

            executableFile = new File(ugRootPath, executableName);

            if (!VSysUtil.isWindows()) {
                try {
                    Process p = Runtime.getRuntime().exec(new String[]{
                        "chmod", "u+x",
                        executableFile.getAbsolutePath()
                    });

                    InputStream stderr = p.getErrorStream();

                    BufferedReader reader
                            = new BufferedReader(
                                    new InputStreamReader(stderr));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        System.out.println("Error: " + line);
                    }

                    p.waitFor();
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(ShellImpl.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
        }

        return executableFile;
    }

    /**
     * Unzips specified source archive to the specified destination folder. If
     * the destination directory does not exist it will be created.
     *
     * @param archive archive to unzip
     * @param destDir destination directory
     * @throws IOException
     */
    public static void unzip(File archive, File destDir) throws IOException {
        IOUtil.unzip(archive, destDir);
    }

    /**
     * Saves the specified stream to file.
     *
     * @param in stream to save
     * @param f destination file
     * @throws IOException
     */
    public static void saveStreamToFile(InputStream in, File f) throws IOException {
        IOUtil.saveStreamToFile(in, f);
    }
}
// based on http://stackoverflow.com/questions/14165517/processbuilder-forwarding-stdout-and-stderr-of-started-processes-without-blocki

class StreamGobbler extends Thread {

    private final InputStream is;
    private final String prefix;
    private final PrintStream pw;

    StreamGobbler(PrintStream pw, InputStream is, String prefix) {
        this.is = is;
        this.prefix = prefix;
        this.pw = pw;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                pw.println(prefix + line);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }
}
