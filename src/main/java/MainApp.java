import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Console;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class MainApp {

    public static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        String pathFrom = "";
        String pathTo = "";
        if (args.length >= 2) {
            pathFrom = args[0];//"c:/test/1";
            pathTo = args[1];//"c:/test/2";
        }

        printStartToEndMSG("Старт копирования");

        if (args.length==3){
            if (args[2].equals("true")){
                try {
                    Utill.deletingAllArchives(pathTo);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                    System.out.println("Ошибка, детальная информация в logs\\error.log");
                }
            }
        }

        if (pathFrom.isEmpty() && pathTo.isEmpty()) {
            System.out.println("Неверные параметры");
            return;
        }

        logger.info("Копирование из папки (" + pathFrom + ") в папку (" + pathTo + ")");
        System.out.println("Копирование из папки (" + pathFrom + ") в папку (" + pathTo + ")");
        try {
            Utill.deletingOldArchives(pathFrom, pathTo);
            Utill.copyFolder(pathFrom, pathTo);
        } catch (IOException e) {
            logger.error(e.getMessage());
            System.out.println("Ошибка, детальная информация в logs\\error.log");
        }
        printStartToEndMSG("Копирование завершено");

        System.out.println("Нажмите [Enter] для завершения...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    private static void printStartToEndMSG(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        logger.info(msg + " - " + dateFormat.format(date));
        System.out.println(msg + " - " + dateFormat.format(date));
    }

}
