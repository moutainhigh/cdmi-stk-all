package pw.cdmi.starlink.core.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LineFileUtils {

    public static String[][] readLine(String filePath, String separator){
        File file = new File(filePath);
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String alineString = null;
        List<String[]> data = new ArrayList<String[]>();
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            int i = 0;
            while ((alineString = bufferedReader.readLine()) != null) {
                String[] row_content = alineString.split(separator);
                data.add(row_content);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (null != bufferedReader)
                    bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != fileReader)
                    fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data.toArray(new String[][]{});
    }

    public static double[][] readLine4Double(String filePath, String separator){
        File file = new File(filePath);
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        String alineString = null;
        List<double[]> data = new ArrayList<double[]>();
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            while ((alineString = bufferedReader.readLine()) != null) {
                String[] row_content = alineString.split(separator);
                double[] doubles_row_content = new double[row_content.length];
                for(int j=0; j< row_content.length; j++){
                    doubles_row_content[j] = Double.parseDouble(row_content[j]);
                }
                data.add(doubles_row_content);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (null != bufferedReader)
                    bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (null != fileReader)
                    fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data.toArray(new double[][]{});
    }
}
