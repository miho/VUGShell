/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.ugshell.vugshell;

import eu.mihosoft.ugshell.vugshell.util.ShellImpl;
import java.io.File;
import java.io.PrintStream;

/**
 * Executes native ugshell.
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public interface Shell {

    /**
     * Destroys the currently running ugshell process.
     */
    void destroy();

    /**
     * Returns the process of the current ugshell execution.
     * @return the process of the current ugshell execution
     */
    Process getProcess();

    /**
     * Returns the working directory
     * @return the working directory
     */
    File getWorkingDirectory();

    /**
     * Prints the ugshell output to the specified print streams.
     * @param out standard output stream
     * @param err error output stream
     * @return this shell
     */
    Shell print(PrintStream out, PrintStream err);

    /**
     * Prints the ugshell output to the standard output.
     * @return this shell
     */
    Shell print();

    /**
     * Waits until the ugshell process terminates.
     * @return this shell
     */
    Shell waitFor();
    
    /**
     * Executes ugshell with the specified script.
     * 
     * @param wd working directory
     * @param script scrit that shall be executed
     * @return this shell
     */
    static Shell execute(File wd, File script) {
        return ShellImpl.execute(wd, script);
    }
    
    /**
     * Executes ugshell with the specified arguments.
     *
     * @param arguments arguments
     * @param wd working directory
     * @return ugshell process
     */
    public static Process execute(File wd, String... arguments) {
        return ShellImpl.execute(false, wd, arguments);
    }
    
}
