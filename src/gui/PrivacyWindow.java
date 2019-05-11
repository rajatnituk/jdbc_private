package gui;

import org.deidentifier.arx.*;
import org.deidentifier.arx.criteria.KAnonymity;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class PrivacyWindow extends  JFrame{


    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JPanel privacyWindow;
    private JComboBox modelName;
    private JTextField suppresionRate;
    private JTable setHeirarchy;
    private JButton nextButton;
    JFileChooser fc;
    ResultSet rs;
    ArrayList<String> attributeList;
    HashMap<String,String> attributeTypeMap;
    HashMap<String, File> HeirarchyPathOfAttribute;
    ArrayList<String[]> data;
    HashMap<String, ArrayList<String[]>> attributeHeirarchyMap;
    String modelName1;
    private JButton updateModelDetails;
    private JTextField KValue;
    private JButton anonymizeButton;
    private JTable resultTable;
    private JList quasiAttributeList;
    private JTable hierarchyTable;
    private JButton addColumnButton;
    private JButton browseForCSVButton;
    private JButton goToModelDetailsButton;
    int suppressionValue;


    Table3Model tm = null;
    HashMap<String, HashSet<String>> attributeDomain;


    public PrivacyWindow(ResultSet rs){
        this.rs = rs;
        data = convertToArrayList(this.rs);
        initializeAttributeList();

        createDomainForAttributes();
        fc = new JFileChooser();
        add(privacyWindow);
        setSize(400,500);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attributeTypeMap = new HashMap<>();
                DefaultTableModel tm = (DefaultTableModel) table1.getModel();
                for(int i=0; i<tm.getRowCount();i++){
                    attributeTypeMap.put(tm.getValueAt(i,0).toString(),tm.getValueAt(i,1).toString());
                }
                tabbedPane1.setSelectedIndex(1);
                System.out.println(attributeTypeMap.toString());
            }
        });
        updateModelDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modelName1 = modelName.getSelectedItem().toString();
                suppressionValue = Integer.parseInt(suppresionRate.getText());
                tabbedPane1.setSelectedIndex(2);
                System.out.println(modelName1+" "+suppressionValue);
                heirarchies();
            }
        });

        if(rs !=null){

            populateAttributeTable();
            //populateHeirarchyTable();
        }
        else{
            sampleSetAttribute();

        }

//
//        anonymizeButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    prepareAnonymizer();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });



        quasiAttributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quasiAttributeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(quasiAttributeList.getSelectedValue()!= null) {
                    if (tm == null) {
                        tm = new Table3Model(attributeDomain, attributeList.get(0));
                        hierarchyTable.setModel(tm);

                    }
                    String changed = (String)quasiAttributeList.getSelectedValue();
                    tm.updateTable(changed);
                }
            }
        });



        addColumnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(tm != null)
                    tm.addColumn();

            }
        });
        browseForCSVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fc.showOpenDialog(tabbedPane1);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    tm.ReadFromCsv(fc.getSelectedFile());

                }
            }
        });

        anonymizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                        convertToRowWiseHierarchy(tm.columnWiseData);
                        prepareAnonymizer();
                        tabbedPane1.setSelectedIndex(3);
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });


    }


    public void sampleSetAttribute(){
        attributeList = new ArrayList<>();
        attributeList.add("abc");
        attributeList.add("gef");
        attributeList.add("hij");
    }


    public void createDomainForAttributes(){
        attributeDomain = new HashMap<>();
        for(int columnNumber = 0; columnNumber < attributeList.size(); columnNumber++ ){
            for(int rowNumber = 0; rowNumber < data.size(); rowNumber++){
                if(attributeDomain.containsKey(attributeList.get(columnNumber))){

                    attributeDomain.get(attributeList.get(columnNumber)).add(data.get(rowNumber)[columnNumber]);

                }
                else{
                    attributeDomain.put(attributeList.get(columnNumber), new HashSet<>());
                }
            }
        }
        //System.out.println(attributeDomain.toString());
    }


    public void initializeAttributeList(){

        try {
            int c = rs.getMetaData().getColumnCount();
            attributeList = new ArrayList<>();
            for (int i = 1; i <= c; i++) {
                attributeList.add(rs.getMetaData().getColumnName(i));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void convertToRowWiseHierarchy(HashMap<String, ArrayList<String[]>> columnWiseData){
        attributeHeirarchyMap = new HashMap<>();
        for(String attribute: columnWiseData.keySet()){
            ArrayList<String[]> tableColumnWise = columnWiseData.get(attribute);
            ArrayList<String[]> tableRowWise = new ArrayList<>();
            for(int rowNumber=0; rowNumber<tableColumnWise.get(0).length; rowNumber++) {
                String[] row = new String[tableColumnWise.size()];
                for(int columnNumber=0; columnNumber<tableColumnWise.size(); columnNumber++)
                    row[columnNumber] = tableColumnWise.get(columnNumber)[rowNumber];
                tableRowWise.add(row);
            }
            attributeHeirarchyMap.put(attribute,tableRowWise);
        }
    }

    public void setResultSet(ResultSet resultSet){
        rs = resultSet;
    }

    public void populateAttributeTable(){
        JComboBox cellValues;
        String[] identifiers = {"Identifying", "Quasi-Identifying", "Insensitive"};
        cellValues = new JComboBox(identifiers);
        DefaultTableModel mod = new DefaultTableModel();
        mod.addColumn("Type");
        mod.addColumn("Attribute");
        table1.setModel(mod);
        TableColumn tc = table1.getColumn("Attribute");
        tc.setCellEditor(new DefaultCellEditor(cellValues));
        for(String x: attributeList) {
            mod.addRow(new Object[]{x,"Insensitive"});
        }

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click to Choose Sensitivity");
        tc.setCellRenderer(renderer);


    }

    public void populateHeirarchyTable(){
            HeirarchyPathOfAttribute = new HashMap<>();
            setHeirarchy.setModel(new AbstractTableModel() {
                private static final long serialVersionUID = 1L;
                private final String[] COLUMN_NAMES = new String[] {"Attribute Name", "Browse"};
                private final Class<?>[] COLUMN_TYPES = new Class<?>[] {String.class, JButton.class};



                @Override
                public int getRowCount() {
                    return attributeList.size();
                }

                @Override
                public int getColumnCount() {
                    return COLUMN_NAMES.length;
                }
                //public int getColumnCount(){ return this}
                @Override public String getColumnName(int columnIndex) {
                    return COLUMN_NAMES[columnIndex];
                }

                @Override public Class<?> getColumnClass(int columnIndex) {
                    return COLUMN_TYPES[columnIndex];
                }

                @Override public Object getValueAt(final int rowIndex, final int columnIndex) {
                    /*Adding components*/
                    switch (columnIndex) {
                        case 0: return attributeList.get(rowIndex);
                        /*Adding button and creating click listener*/
                        case 1: JButton button = new JButton("Browse");
                            button.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    int returnVal = fc.showOpenDialog(tabbedPane1);
                                    if(returnVal == JFileChooser.APPROVE_OPTION){
                                            File file = fc.getSelectedFile();
                                            HeirarchyPathOfAttribute.put(attributeList.get(rowIndex),file);

                                        System.out.println(HeirarchyPathOfAttribute);
                                    }

                                }
                            });
                            return button;
                        default: return "Error";
                    }
                }
            });

        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        setHeirarchy.getColumn("Browse").setCellRenderer(buttonRenderer);
        setHeirarchy.addMouseListener(new JTableButtonMouseListener(setHeirarchy));
        setHeirarchy.updateUI();

    }

    //It create a ArrayList of QuasiIdentifying ATTRIBUTES and initializes the list with these values
    public void heirarchies(){
        ArrayList<String> quasiIdentifyingAttributes = new ArrayList<>();
        Set<String> keys = attributeTypeMap.keySet();
        Iterator<String> i = keys.iterator();
        while(i.hasNext()){
            String attribute = i.next();
            if(attributeTypeMap.get(attribute).equals("Quasi-Identifying"))
                quasiIdentifyingAttributes.add(attribute);
        }
        System.out.println(quasiIdentifyingAttributes);
        ListModel<String> lm = new AbstractListModel<String>() {
            @Override
            public int getSize() {
                return quasiIdentifyingAttributes.size();
            }

            @Override
            public String getElementAt(int index) {
                return quasiIdentifyingAttributes.get(index);
            }
        };

        quasiAttributeList.setModel(lm);
        quasiAttributeList.setSelectedIndex(0);
    }

    public void prepareAnonymizer() throws IOException {

        Data arxData = Data.create(data.iterator());
        for(String attribute: attributeList){
            arxData.getDefinition().setDataType(attribute, DataType.STRING);
            switch (attributeTypeMap.get(attribute)){
                case "Sensitive": arxData.getDefinition().setAttributeType(attribute, AttributeType.SENSITIVE_ATTRIBUTE);
                                    break;
                case "Insensitive": arxData.getDefinition().setAttributeType(attribute, AttributeType.INSENSITIVE_ATTRIBUTE);
                    break;
                case "Identifying": arxData.getDefinition().setAttributeType(attribute, AttributeType.IDENTIFYING_ATTRIBUTE);
                    break;
                case "Quasi-Identifying": arxData.getDefinition().setAttributeType(attribute, AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
                    break;

            }
            //Heirarchy
//            if(HeirarchyPathOfAttribute.get(attribute)!=null) {
//                arxData.getDefinition().setHierarchy(attribute, AttributeType.Hierarchy.create(
//                        HeirarchyPathOfAttribute.get(attribute),
//                        Charset.defaultCharset(),
//                        ','
//                ));
//            }
            for(String attibute: attributeHeirarchyMap.keySet()){
                AttributeType.Hierarchy hierarchy = AttributeType.Hierarchy.create(attributeHeirarchyMap.get(attibute).iterator());
                arxData.getDefinition().setHierarchy(attibute,hierarchy);
            }

        }


        //Configuration
        ARXConfiguration configuration = ARXConfiguration.create();
        configuration.setSuppressionLimit(suppressionValue/100d);
        configuration.addPrivacyModel(new KAnonymity(Integer.parseInt(KValue.getText())));

        //Anonymize
        try {
            ARXAnonymizer anonymizer = new ARXAnonymizer();
            ARXResult result = anonymizer.anonymize(arxData, configuration);

            if(result.isResultAvailable()) {
                System.out.println("YES");
                resultTable.setModel(new AbstractTableModel() {
                    DataHandle handle = result.getOutput();

                    @Override
                    public int getRowCount() {
                        return handle.getNumRows();
                    }

                    @Override
                    public String getColumnName(int i){
                        return handle.getAttributeName(i);
                    }

                    @Override
                    public int getColumnCount() {
                        return handle.getNumColumns();
                    }

                    @Override
                    public Object getValueAt(int rowIndex, int columnIndex) {
                        return handle.getValue(rowIndex,columnIndex);
                    }

                    @Override
                    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
                        resultTable.setValueAt(aValue,rowIndex,columnIndex);
                    }

                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return true;
                    }
                });
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    //convert resultset into ArrayList<String[]> to put it into DataSource of Anonymizer
    public ArrayList<String[]> convertToArrayList(ResultSet rs){
        ArrayList<String[]> data = new ArrayList<String[]>();

        try {
            String[] attributes = new String[rs.getMetaData().getColumnCount()];
            for(int i=1; i<=rs.getMetaData().getColumnCount(); i++)
                attributes[i-1] = rs.getMetaData().getColumnName(i);
            data.add(attributes);
            rs.beforeFirst();
            int count = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                //System.out.print("yes");
                String[] row = new  String[count];
                for(int i=1; i<=count; i++)
                    row[i-1] = rs.getString(i);
                data.add(row);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return data;

    }


}


class JTableButtonRenderer implements TableCellRenderer {
    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JButton button = (JButton)value;
        return button;
    }
}


class JTableButtonMouseListener extends MouseAdapter {
    private final JTable table;

    public JTableButtonMouseListener(JTable table) {
        this.table = table;
    }

    public void mouseClicked(MouseEvent e) {
        int column = table.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
        int row    = e.getY()/table.getRowHeight(); //get the row of the button

        /*Checking the row or column is valid or not*/
        if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
            Object value = table.getValueAt(row, column);
            if (value instanceof JButton) {
                /*perform a click event*/
                ((JButton)value).doClick();
            }
        }
    }
}





