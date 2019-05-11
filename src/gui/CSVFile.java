package gui;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

class CSVFile {
    private final ArrayList<String[]> Rs = new ArrayList<String[]>();
    private String[] OneRow;
    public ArrayList<String[]> ReadCSVfile(File DataFile, String dilimiter) {
        try {
            BufferedReader brd = new BufferedReader(new FileReader(DataFile));
            int count = 0;
            while (brd.ready()) {
                String st = brd.readLine();
                OneRow = st.split(Pattern.quote(dilimiter));
//                for(String x:OneRow)
//                    System.out.print(x+" ");
//                System.out.print("\n");
                Rs.add(OneRow);
                count++;
                //System.out.println(Arrays.toString(OneRow));
            } // end of while
        } // end of try
        catch (Exception e) {
            String errmsg = e.getMessage();
            System.out.println("File not found:" + errmsg);
        } // end of Catch
        return Rs;
    }// end of ReadFile method
}// end of CSVFile class
