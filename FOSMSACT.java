import java.util.Scanner;
import java.util.Set;
import java.util.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.NumberFormat;

import com.healthmarketscience.jackcess.*;
import com.healthmarketscience.jackcess.Database.FileFormat;

public class FOSMSACT
{
    public static void main (String[] args)
    {
        boolean verbose = false;
        String inname = "";
        String outname = "";
        String revnum = "";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-v") || args[i].equals("--verbose"))
            {
                verbose = true;
            }
            if (args[i].equals("-h") || args[i].equals("--help"))
            {
                System.out.println("Thank you for using the FOSS MSAccess Conversion Tool!\n"
                                  +"\n"
                                  +"The command line options are: \n"
                                  +"    [ -h || --help ]        if you need help.\n"
                                  +"    [ -i || --filename ]    input filename        (REQUIRED)\n"
                                  +"    [ -o || --output ]      output filename       (REQUIRED)\n"
                                  +"    [ -r || --revisionnum ] MS Access Revision    (will default to 2016 if not specified)\n"
                                  +"Options for revision number:\n"
                                  +"    2000\n"
                                  +"    2003\n"
                                  +"    2007\n"
                                  +"    2010\n"
                                  +"    2016\n"
                                  +"[ -v || --verbose ]     Print file contents\n"
                                  +"\n"
                                  +"If you're converting to an Access file, make sure the input CSV\n"
                                  +"or TXT file has the following, in addition to the data you wish to parse:\n"
                                  +"-A table name header per table\n"
                                  +"-The column header names of each column per table\n"
                                  +"-The proper column data types of those columns for Access to read\n"
                                  +"-And after your last data row, a row containing only the delimiter string:\n"
                                  +"'?NEWTABLE?', with no quotes.");
                System.exit(0);
            }
            if (args[i].equals("-i") || args[i].equals("--input"))
            {
                inname = args[i+1];
                i++;
            }
            if (args[i].equals("-o") || args[i].equals("--output"))
            {
                outname = args[i+1];
                i++;
            }
            if (args[i].equals("-r") || args[i].equals("--revisionnum"))
            {
                revnum = args[i+1];
                i++;
            }
        }
        String[] in = inname.split("\\.");
        System.out.print(inname);
        String[] out = outname.split("\\.");
        System.out.print(outname);
        System.out.println(in[1]);

        if (in[in.length-1].equals("accdb") && 
           (out[out.length-1].equals("csv") || out[out.length-1].equals("CSV") 
           || 
            out[out.length-1].equals("txt") || out[out.length-1].equals("TXT"))) // if for whatever reason there are two .'s in the pathname
        {
            WriteFromAccDB(inname, outname, verbose);
        }
        else if (out[out.length-1].equals("accdb") && 
                (in[in.length-1].equals("csv") || in[in.length-1].equals("CSV") 
                || 
                in[in.length-1].equals("txt") || in[in.length-1].equals("TXT"))) 
        {
            WriteToAccDB(inname, outname, revnum, verbose);
        }
    }
    private static void WriteFromAccDB(String inname, String outname, boolean verbose)
    {
        String delimiter = ",";
        try
        {
            Database db = DatabaseBuilder.open(new File(inname));
            Set<String> tableNames = db.getTableNames();
            ArrayList<String> al = new ArrayList<String>();  
            PrintWriter writer = new PrintWriter(outname, "UTF-8");
            for (Iterator<String> it = tableNames.iterator(); it.hasNext(); ) {
                String s = it.next();
                Table table = db.getTable(s);
                if (verbose)
                {
                    writer.println(s);
                }
                else
                {
                    writer.write(s+"\n");
                }
                for (int j = 0; j <= 1; j++)
                {
                    int i = 0;
                    for(Column column : table.getColumns()) 
                    {
                        if (j == 0)
                        {
                            s = column.getName();
                        }
                        else if (j == 1)
                        {
                            s = column.getType().toString();
                        }         
                        al.add(s);
                        if (verbose)
                        {
                            writer.print(s);
                        }
                        else
                        {
                            writer.write(s);
                        }
                        if (i <= table.getColumnCount()-2)
                        {
                            if (verbose)
                            {
                                writer.print(delimiter);
                            }
                            else
                            {
                                writer.write(delimiter);
                            }                           
                        }
                        i++;
                    }
                    if (verbose)
                    {
                        writer.println();
                    }
                    else
                    {
                        writer.write("\n");
                    }
                }
                for (Row row: table)
                {
                    for (int j = 0; j < table.getColumnCount(); j++)
                    {
                        Object value = row.get(al.get(j));
                        System.out.print(value);
                        if (value != null)
                        {            
                            if (verbose)
                            {
                                writer.print(value);
                            }
                            else
                            {
                                writer.write(value.toString());
                            }
                        }
                        if (j <= table.getColumnCount()-2)
                        {
                            if (verbose)
                            {
                                writer.print(delimiter);
                            }
                            else
                            {
                                writer.write(delimiter);
                            }  
                        }  
                    }
                    if (verbose)
                    {
                        writer.println();
                    }
		            else
                    {
                        writer.write("\n");
                    }
                }
                if (verbose)
                {
                    writer.println("?NEWTABLE?");               }
                else
                {
                    writer.write("?NEWTABLE?\n");
                }
            }
            writer.close();
            db.close();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("The file does not exist or has been renamed. Please supply an ACCDB file.");
        }
        catch (IOException e)
        {
            System.out.println("Something happened! Can't write the contents of the database to the file!");
            e.printStackTrace();
        }
    }
    private static void WriteToAccDB(String inname, String outname, String revision, boolean verbose)
    {
        File file = new File(inname);
        ArrayList<String[]> lines = new ArrayList<String[]>();
        try
        {
            String line = "";
            Database db;
            BufferedReader br = new BufferedReader(new FileReader(inname));        
            while ((line = br.readLine()) != null) 
            {
                // use comma as separator
                String[] content = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                lines.add(content);
            }
            File database = new File(outname);
            if (revision == "2000")
            {
                db = new DatabaseBuilder(database)
                .setFileFormat(Database.FileFormat.V2000)
                .create();
            }
            else if (revision == "2003")
            {
                db = new DatabaseBuilder(database)
                .setFileFormat(Database.FileFormat.V2003)
                .create();
            }
            else if (revision == "2007")
            {
                db = new DatabaseBuilder(database)
                .setFileFormat(Database.FileFormat.V2007)
                .create();
            }
            else if (revision == "2010")
            {
                db = new DatabaseBuilder(database)
                .setFileFormat(Database.FileFormat.V2010)
                .create();
            }
            else // default condition
            {
                db = new DatabaseBuilder(database)
                .setFileFormat(Database.FileFormat.V2016)
                .create();
            }
            int k = 0; 
            while (k < lines.size())
            {
                String[] charsToRead = lines.get(k);
                k++;                
                String tableName = charsToRead[0];
                System.out.print(tableName);
                System.out.println();

                charsToRead = lines.get(k);
                k++;
                String[] charsDataTypes = lines.get(k);
                k++;
                Table table;
                if (charsDataTypes[0].equals("TEXT"))
                {
                    table = new TableBuilder(tableName)
                    .addColumn(new ColumnBuilder(charsToRead[0], DataType.TEXT))
                    .toTable(db);
                }
                else if (charsDataTypes[0].equals("SHORT_DATE_TIME"))
                {
                    table = new TableBuilder(tableName)
                    .addColumn(new ColumnBuilder(charsToRead[0], DataType.SHORT_DATE_TIME))
                    .toTable(db);
                }
                else if (charsDataTypes[0].equals("MONEY"))
                {
                    table = new TableBuilder(tableName)
                    .addColumn(new ColumnBuilder(charsToRead[0], DataType.MONEY))
                    .toTable(db);
                }
                else if (charsDataTypes[0].equals("LONG"))
                {
                    table = new TableBuilder(tableName)
                    .addColumn(new ColumnBuilder(charsToRead[0], DataType.LONG))
                    .toTable(db);
                }
                else
                {
                    table = new TableBuilder(tableName)
                    .addColumn(new ColumnBuilder(charsToRead[0], DataType.MEMO))
                    .toTable(db);
                }
                for (int i = 1; i < charsDataTypes.length; i++)
                {
                    System.out.println(charsDataTypes[i]);
                    if (charsDataTypes[i].equals("SHORT_DATE_TIME"))
                    {
                        new ColumnBuilder(charsToRead[i])
                        .setType(DataType.SHORT_DATE_TIME)
                        .addToTable(db.getTable(tableName));
                    }
                    else if (charsDataTypes[i].equals("MONEY"))
                    {
                        new ColumnBuilder(charsToRead[i])
                        .setType(DataType.MONEY)
                        .addToTable(db.getTable(tableName));
                    }
                    else if (charsDataTypes[i].equals("TEXT"))
                    {
                        new ColumnBuilder(charsToRead[i])
                        .setType(DataType.TEXT)
                        .addToTable(db.getTable(tableName));
                    }
                    else if (charsDataTypes[i].equals("LONG"))
                    {
                        new ColumnBuilder(charsToRead[i])
                        .setType(DataType.LONG)
                        .addToTable(db.getTable(tableName));
                    }
                    else
                    {
                        new ColumnBuilder(charsToRead[i])
                        .setType(DataType.MEMO)
                        .addToTable(db.getTable(tableName));
                    }
                } 
                String[] nextInput = lines.get(k);
                k++;
                while (!nextInput[0].equals("?NEWTABLE?"))
                {
                    Object[] row = new Object[nextInput.length + 1];
                    int i = 0;             
                    for (int j = 0; j < nextInput.length; j++)
                    {
                        //if (j == 0)
                        //{
                        //    row[j] = i;
                        //    if (verbose)
                        //    {
                        //        System.out.print(i);
                        //    }
                        //}
                        //else
                        //{
                    if (charsDataTypes[j].equals("SHORT_DATE_TIME"))
                    {
 
                        String dateInString = nextInput[j];
                        Date date = new Date(dateInString);
                        row[j] = date;
                    }
                    else if (charsDataTypes[j].equals("MONEY"))
                    {
                        row[j] = nextInput[j];                        
                    }
                    else if (charsDataTypes[j].equals("TEXT"))
                    {
                        row[j] = nextInput[j];
                    }
                    else if (charsDataTypes[j].equals("LONG"))
                    {
                        row[j] = Double.parseDouble(nextInput[j]);
                    }
                    else
                    {
                        row[j] = nextInput[j];
                    }
                            
                            if (verbose)
                            {
                                System.out.print(row[j]);  
                            }              
                        //}  
                        System.out.println(j); 
                    }
                    i++;
                    System.out.println(i);
                    if (verbose)
                    {
                        System.out.println("Added Row to ACCDB");
                    }
                    table.addRow(row);
                    nextInput = lines.get(k);
                    k++;
                }
                System.out.println("Done with this table");

            }
            db.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("The file does not exist or has been renamed. Please supply a TXT or CSV file.");
        }
        catch (IOException e)
        {
            System.out.println("Something happened! Can't write the contents of the file to the database!");
            e.printStackTrace();
        }
        //catch (ParseException e)
        //{
        //    System.out.println("Invalid Input! Please check Dates and Numbers!");
        //    e.printStackTrace();
        //}
    }
}

