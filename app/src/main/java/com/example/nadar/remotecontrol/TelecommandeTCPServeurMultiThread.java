package com.example.nadar.remotecontrol;

import java.net.*;
import java.io.*;
import java.awt.*;
import java.util.Scanner;
import java.awt.event.*;
import java.awt.Robot;

public class TelecommandeTCPServeurMultiThread{
    private static Robot robot;
    private static long lastTop = System.currentTimeMillis();

    public static void main(String[] args) throws Exception{
        int port = 80;
        long délai = 30*60*1000;
        try{
            port  = Integer.parseInt(args[0]);
            délai = Integer.parseInt(args[1])*60*1000;
        }catch(Exception e){}

        ServerSocket serveur = new ServerSocket(port);
        System.out.println("-- TelecommandeTCPServeurMultiThread-4/12/2010 est un serveur en mode TCP, port " + port);
        System.out.println("--  Une commande valide déclenche un déplacement du curseur, une tabulation, un click ou un déplacement de la souris, etc... ");
        System.out.println("--     envoi d'une commande par ligne {next|n|previous|p|up|u|down|d|tab|t|click|clickDouble|move x y|beep|mousePress|mouseRelease|getScreenSize|exit}\\n*");
        System.out.println("--       - la commande {getScreenSize\\n} retourne {width height} de l'écran distant ");
        System.out.println("--       - la commande {exit\\n}, engendre une déconnexion");
        System.out.println();
        System.out.println("--  TelecommandeTCPServeurMultiThread est prêt, et en attente d'une requête");
        System.out.println("--   Adresse IP:port du service : " + InetAddress.getLocalHost().getHostAddress() + ":" + port + ", arret au bout de " + (délai/(60*1000)) + "mn d'inactivité." );
        System.out.println("-- -----------------------------------------------------------------------------------------");
        System.out.println();

        new DélaiDeGarde(délai);

        robot = new Robot();
        robot.setAutoWaitForIdle(true);
        while(true) {
            Socket socket = serveur.accept();
            new Connexion(socket);
        }
    }

    private static class DélaiDeGarde extends Thread{
        private static long lastTop = System.currentTimeMillis();
        private long délai;
        public DélaiDeGarde(long délai){
            this.délai = délai;
            start();
        }
        public void run(){
            while(true){
                try{
                    Thread.sleep(30*1000);
                    if(((System.currentTimeMillis()-lastTop)+30*1000) >= délai){
                        System.out.println("-- arret du serveur dans environ 30 secondes...");
                        Thread.sleep(31*1000);
                        if((System.currentTimeMillis()-lastTop) >= délai){
                            System.exit(0);
                        }
                    }
                }catch(InterruptedException ie){}
            }
        }
    }

    private static class Connexion extends Thread{
        private Socket socket;
        private String client;

        public Connexion(Socket socket){
            this.socket = socket;
            this.client = socket.getInetAddress().getHostAddress() + "/" + socket.getInetAddress().getHostName();
            start();
        }
        public void run(){
            BufferedReader in = null;
            DataOutputStream out = null;
            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out= new DataOutputStream( socket.getOutputStream());

                String str = new String("commande de [" + client + "] : ");
                boolean interrompu = false;
                while(!(interrompu)) {
                    String cmd = in.readLine();
                    System.out.println(str + (cmd==null?client + " disconnected":cmd));
                    lastTop = System.currentTimeMillis();

                    if(cmd==null || cmd.equals("exit") || cmd.equals("e")){
                        interrompu = true;
                    }else if(cmd.equals("next") || cmd.equals("n")){
                        keyPressAndRelease(KeyEvent.VK_RIGHT);
                    }else if (cmd.equals("previous") || cmd.equals("p")){
                        keyPressAndRelease(KeyEvent.VK_LEFT);
                    }else if (cmd.startsWith("move ")){ // move X Y
                        Scanner sc = new Scanner(cmd);
                        sc.next();
                        mouseMove(sc.nextInt(),sc.nextInt());
                    }else if (cmd.equals("click")){
                        mousePressAndRelease();
                    }else if (cmd.equals("clickDouble")){
                        mousePressAndRelease();mousePressAndRelease();
                    }else if (cmd.equals("mousePress")){
                        mousePress();
                    }else if (cmd.equals("mouseRelease")){
                        mouseRelease();
                    }else if(cmd.equals("beep") || cmd.equals("b")){
                        Toolkit.getDefaultToolkit().beep();
                        robot.delay(20);
                    }else if(cmd.equals("getScreenSize")){
                        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                        out.write(new String(dim.width + " " + dim.height +"\n").getBytes());
                        out.flush();
                    }else if(cmd.equals("System.exit(0);")){
                        System.exit(0);// sortie brutale
                    }else{
                        System.out.println("commande recue : " + cmd + " est inconnue ");
                    }
                }

            }catch(IOException e){
                System.out.println("exception : " + e.getMessage());
            }finally{
                try{
                    in.close();
                    out.close();
                    socket.close();
                }catch(Exception e){}
            }
        }
    }

    private static void mouseMove(int x, int y){
        synchronized(robot){ robot.mouseMove(x,y);}
    }

    private static void mousePress(){
        synchronized(robot){
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.delay(10);
        }
    }

    private static void mouseRelease(){
        synchronized(robot){
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            robot.delay(10);
        }
    }

    private static void mousePressAndRelease(){
        mousePress();
        mouseRelease();
    }

    private static void keyPressAndRelease(int key){
        synchronized(robot){
            robot.keyPress(key);
            robot.delay(10);
            robot.keyRelease(key);
            robot.delay(10);
        }
    }
}