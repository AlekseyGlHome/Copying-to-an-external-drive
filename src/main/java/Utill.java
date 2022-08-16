import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Utill {

    private static long countFile = -1;
    private static long countCopy = 1;

    public static void deletingAllArchives(String destinationDirectory) throws IOException {

        File[] filesTo = new File(destinationDirectory).listFiles();
        if (filesTo == null) {
            System.out.println("Папка приемник ненайдена " + destinationDirectory);
            MainApp.logger.error("Папка приемник ненайдена " + destinationDirectory);
            return;
        }

        for (File fileTo : filesTo) {
            if (!fileTo.isFile()) {
                File folderNew = new File(destinationDirectory + "\\" + fileTo.getName());
                deletingAllArchives(folderNew.getAbsolutePath());
            }
            System.out.println("Удаляется из внешнего архива старый файл (" + fileTo.getAbsolutePath() + ")");
            MainApp.logger.info("Удаляется из внешнего архива старый файл (" + fileTo.getAbsolutePath() + ")");
            deleteFile(fileTo);
        }
    }


    public static void deletingOldArchives(String sourceDirectory, String destinationDirectory) throws IOException {
        File[] filesFrom = new File(sourceDirectory).listFiles();
        File[] filesTo = new File(destinationDirectory).listFiles();
        if (filesTo == null) {
            System.out.println("Папка приемник ненайдена " + destinationDirectory);
            MainApp.logger.error("Папка приемник ненайдена " + destinationDirectory);
            return;
        }
        if (filesFrom == null) {
            System.out.println("Папка источник ненайдена " + sourceDirectory);
            MainApp.logger.error("Папка источник ненайдена " + sourceDirectory);
            return;
        }

        for (File fileTo : filesTo) {
            boolean isFound = false;
            File file_ = new File(sourceDirectory);
            for (File fileFrom : filesFrom) {
                if (equals(fileTo, fileFrom)) {
                    isFound = true;
                    file_ = fileFrom;
                    break;
                }
            }
            if (!fileTo.isFile()) {
                File folderNew = new File(destinationDirectory + "\\" + fileTo.getName());
                deletingOldArchives(file_.getAbsolutePath(), folderNew.getAbsolutePath());
            }
            if (!isFound) {
                System.out.println("Удаляется из внешнего архива старый файл (" + fileTo.getAbsolutePath() + ")");
                MainApp.logger.info("Удаляется из внешнего архива старый файл (" + fileTo.getAbsolutePath() + ")");
                deleteFile(fileTo);
            }
        }
    }


    private static long getCountFile(String sourceDirectory, String destinationDirectory) {
        File[] files = new File(sourceDirectory).listFiles();

        long count = 0;
        if (files != null) {

            for (File file : files) {
                String fileName = destinationDirectory + "\\" + file.getName();
                if (file.isFile()) {
                    if (Files.notExists(Paths.get(fileName))) {
                        count++;
                    }
                } else {
                    File folderNew = new File(fileName);
                    count += getCountFile(file.getAbsolutePath(), folderNew.getAbsolutePath());
                }
            }
        } else {
            return 0;
        }
        return count;
    }

    public static void copyFolder(String sourceDirectory, String destinationDirectory) throws IOException {
        File[] files = new File(sourceDirectory).listFiles();
        File fileNew = new File(destinationDirectory);
        if (countFile == -1) {
            countFile = getCountFile(sourceDirectory, destinationDirectory);
        }
        if (files != null) {
            fileNew.mkdir();

            for (File file : files) {
                String fileName = destinationDirectory + "\\" + file.getName();
                if (file.isFile()) {
                    if (Files.notExists(Paths.get(fileName))) {
                        System.out.println("Копируется файла [" + countCopy + " из " + countFile + "] (" + file.getAbsolutePath() + ") в (" + fileName + ")");
                        MainApp.logger.info("Копируется файла [" + countCopy + " из " + countFile + "] (" + file.getAbsolutePath() + ") в (" + fileName + ")");
                        copyFileStrim(file, new File(fileName));
                        countCopy++;
                    }
                } else {
                    File folderNew = new File(fileName);
                    copyFolder(file.getAbsolutePath(), folderNew.getAbsolutePath());
                }
            }
        } else {
            return;
        }
    }

    private static void copyFileStrim(File fileFrom, File fileTo) throws IOException {
        int percentProgress = 50;
        long sizeFile;
        long freeSpace;
        int countChar = 0;

        try (InputStream is = new FileInputStream(fileFrom); OutputStream os = new FileOutputStream(fileTo)) {
            sizeFile = fileFrom.length();
            freeSpace = fileTo.getFreeSpace();
            if (freeSpace < sizeFile) {
                System.out.println("Копирование отменено: " + fileFrom.getAbsolutePath() +
                        " Недостаточно свободного места " + fileTo.getAbsolutePath());
                MainApp.logger.error("Копирование отменено: " + fileFrom.getAbsolutePath() +
                        " Недостаточно свободного места " + fileTo.getAbsolutePath());
                return;
            }
            printHederProgress(percentProgress);
            long coutProgress = sizeFile / percentProgress;
            long sizeCopy = 0L;

            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
                sizeCopy += length;
                if (sizeCopy >= coutProgress) {
                    sizeCopy = 0;
                    System.out.print("#");
                    countChar++;
                }
            }
            os.flush();
        } finally {
            if (countChar != percentProgress) {
                for (int i = 0; i < percentProgress - countChar; i++) {
                    System.out.print("#");
                }
            }
        }
        System.out.println();
    }

    private static void printHederProgress(int percentProgress) {
        System.out.print("\t[");
        for (int i = 0; i < percentProgress; i++) {
            System.out.print("-");
        }
        System.out.print("]");
        System.out.println();
        System.out.print("\t ");
    }


//    private static void copyFile(File fileFrom, File fileTo) throws IOException {
//        FileChannel inputChannel = null;
//        FileChannel outputChannel = null;
//        try {
//            inputChannel = new FileInputStream(fileFrom).getChannel();
//            outputChannel = new FileOutputStream(fileTo).getChannel();
//            System.out.println("Копирование файла " + fileFrom.getAbsolutePath());
//            MainApp.logger.info("Копирование файла " + fileFrom.getAbsolutePath());
//            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
//        } finally {
//            inputChannel.close();
//            outputChannel.close();
//        }
//
//    }


    private static void deleteFile(File file) throws IOException {
        Files.delete(Paths.get(file.getAbsolutePath()));

    }

    private static boolean equals(File fileTo, File fileFrom) {
        return fileTo.getName().equals(fileFrom.getName());
    }
}
