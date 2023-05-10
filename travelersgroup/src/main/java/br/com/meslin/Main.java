package br.com.meslin;

import org.apache.commons.cli.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static String username;	// nome do usuário
    private static String filename;	// nome do arquivo com o caminho

    public static void main(String[] args) {
        // parse da linha de comando
        String dir = "/home/josivan/IdeaProjects/travelersgroup/src/main/java/br/com/meslin/";

		String[] files = {"caminho.txt", "caminho2.txt", "caminho3.txt"};

//        String[] files = {"caminho2.txt"};
        for (int i=0; i<files.length; i++)
        {
            filename = dir+files[i];
            username = "usuário" + (int)(Math.random() * Integer.MAX_VALUE);
            Options opcoes = new Options();
            opcoes.addOption("f", "filename", true, "nome do arquivo com o caminho do usuario");
            opcoes.addOption("u", "username", true, "nome do usuário");
            CommandLineParser parser = new DefaultParser();
            try
            {
                CommandLine cmd = parser.parse(opcoes, args);
                if(cmd.hasOption('f')) filename = cmd.getOptionValue('f');
                if(cmd.hasOption("filename")) filename = cmd.getOptionValue("filename");
                if(cmd.hasOption('u')) username = cmd.getOptionValue('u');
                if(cmd.hasOption("username")) username = cmd.getOptionValue("username");
            }
            catch (ParseException e)
            {
                System.err.println("Erro durante o parse da linha de comandos. Motivo: " + e.getMessage());
                System.err.println("Uso: ClienteMovel -f <arquivo com caminho> -u <nome do usuario>");
                return;
            }

            Logger.getLogger("").setLevel(Level.OFF);
            new ClienteMovel(username, filename);
        }


    }
}