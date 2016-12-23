/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.ugshell;

import eu.mihosoft.ugshell.util.ShellImpl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class ShellTest {

    @Test
    public void executeExampleScriptTest() throws IOException {

        File tmpDir = Files.createTempDirectory("ugshelltest").toFile();
        System.out.println("tmpDir: " + tmpDir);
        File example = new File(tmpDir, "example.zip");
        ShellImpl.saveStreamToFile(
                getClass().getResourceAsStream("/eu/mihosoft/ugshell/example.zip"),
                example);
        ShellImpl.unzip(example, tmpDir);
        
        File exampleDir = new File(tmpDir,"example");

        boolean noError = Shell.execute(exampleDir,
                new File(exampleDir,"laplace.lua")).
                print(System.out, System.err).waitFor().
                getProcess().exitValue() == 0;
        
        Assert.assertTrue("Execution must not fail", noError);
        
        Assert.assertTrue("File A2d.mat must exist",
                new File(exampleDir,"A2d.mat").exists());
        Assert.assertTrue("File rhs_laplace2d.vec must exist",
                new File(exampleDir,"rhs_laplace2d.vec").exists());
        Assert.assertTrue("sol_laplace_2d.vec",
                new File(exampleDir,"sol_laplace_2d.vec").exists());
        Assert.assertTrue("File sol_laplace_2d.vtu must exist",
                new File(exampleDir,"sol_laplace_2d.vtu").exists());

    }
}
