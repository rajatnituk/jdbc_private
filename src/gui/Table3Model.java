package gui;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.*;

class Table3Model extends AbstractTableModel {
    HashMap<String, HashSet<String>> local;
    HashMap<String, ArrayList<String[]>> columnWiseData;
    String currentAttribute;
    ArrayList<String[]> currentData;


    public Table3Model(HashMap<String, HashSet<String>> local, String currentAttribute){
        this.local = local;
        columnWiseData = new HashMap<>();
        processColumnWiseData(local);
        currentData = columnWiseData.get(currentAttribute);
        this.currentAttribute = currentAttribute;

    }

    public void processColumnWiseData(HashMap<String, HashSet<String>> local){
        Set<String> attributes = local.keySet();
        Iterator<String> iterator = attributes.iterator();
        while(iterator.hasNext()){
            String attribute = iterator.next();
            HashSet<String> columnValues = local.get(attribute);
            Iterator<String> iterator1 = columnValues.iterator();
            ArrayList<String[]> attributeData = new ArrayList<>();

            String[] firstColumn =  new String[columnValues.size()+1];
            int count = 1;
            firstColumn[0] = "level 0";
            while(iterator1.hasNext()){
                firstColumn[count++] = iterator1.next();
            }
            attributeData.add(firstColumn);
            columnWiseData.put(attribute,attributeData);
        }

    }

    @Override
    public int getRowCount() {
        return currentData.get(0).length-1;
    }

    @Override
    public int getColumnCount() {
        int i = currentData.size();
        //System.out.println(i);
        return i;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return currentData.get(columnIndex)[0];
    }



    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object i =  currentData.get(columnIndex)[rowIndex+1];
        //System.out.println((String)i);
        return i;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        currentData.get(columnIndex)[rowIndex+1] = (String)aValue;
        this.fireTableDataChanged();
    }

    public void updateTable(String attributeSelected){
        currentAttribute = attributeSelected;
        currentData = columnWiseData.get(currentAttribute);
        this.fireTableStructureChanged();

    }

    public void addColumn(){
        String ColumnName = "level"+getColumnCount();
        String[] data = new String[currentData.get(0).length];
        data[0] = ColumnName;
        for(int i=1; i<data.length; i++)
            data[i] = "*";
        currentData.add(data);
        this.fireTableStructureChanged();
    }

    public void ReadFromCsv(File file){
        CSVFile Rd = new CSVFile();
        currentData = new ArrayList<>();
        ArrayList<String[]> data = Rd.ReadCSVfile(file, ",");
        if (data.get(0).length != 0) {
            int columnSize = data.get(0).length;
            int rowSize = data.size();
            for(int i=0; i< columnSize; i++){
                String[] tmp = new String[rowSize+1];
                tmp[0] = "level"+i;
                for(int j=1; j< rowSize+1; j++){
                    tmp[j] = data.get(j-1)[i];
                }
                currentData.add(tmp);
            }
            columnWiseData.put(currentAttribute,currentData);
            this.fireTableStructureChanged();
        }
    }
}
