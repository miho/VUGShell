package eu.mihosoft.ugshell;

import java.io.File;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Shell.execute(new File("example"), new File("example/laplace.lua")).
                print(System.out, System.err).waitFor();
    }
}
