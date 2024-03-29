# FOSS-MSAccess-Conversion-Tool
Free and Open Source Microsoft Access File Conversion Tool
--------------------------------------------------------------------------------
                                    README                                     
--------------------------------------------------------------------------------                                                  
- 09/13/2019                                                                   
- Free and Open Source Software for MicroSoft Access File Conversion                                               
- FOSMSACT                                                  
--------------------------------------------------------------------------------
                                     ABOUT                                     
--------------------------------------------------------------------------------
- This program is designed to convert between MicroSoft Access files and LibreOffice-readable file formats. These include but are not limited to
              
              - .csv
              - .txt
- This program can be executed from the CLI (Command-Line Interface).
       
- This application relies heavily on the Jackcess API, whose link is provided
       below:

- This project (technically) lets you view your ACCDB files. Just run in
       verbose mode and the terminal will spit out the contents of your files.

--------------------------------------------------------------------------------
                                   PREREQUISITES                               
--------------------------------------------------------------------------------
- Installed Programs:                                                          
    - Java Ver. 8+. It does not matter who published it (i.e. both OpenJDK and
        Oracle Java worked for me).
--------------------------------------------------------------------------------
                                 INSTALL AND RUN                               
--------------------------------------------------------------------------------
- Installation Steps:                                                          
              - Install prerequisite programs with your OS's installation      
                schema (e.g. sudo pacman -S java).
                (Note: for compatibility reasons, required Java Libraries are included)
              - Compile the code with: 
```javac -cp ".:jackcess-3.0.1.jar:commons-lang3-3.9.jar:commons-logging-1.2:commons-logging-1.2.jar:poi-4.0.0:poi-4.0.0.jar" FOSMSACT.java```
- To Run:        
              - Run the code with:
```java -cp ".:jackcess-3.0.1.jar:commons-lang3-3.9.jar:commons-logging-1.2:commons-logging-1.2.jar:poi-4.0.0:poi-4.0.0.jar;" FOSMSACT```
              
              CLI PARAMETERS:
                  [-h || --help        ] if you need help.
                  [-i || --input       ] input filename            (REQUIRED)
                  [-o || --output      ] output filename           (REQUIRED)
                  [-r || --revisionnum ] MS Access Revision        (will default to 2016 if not specified)
                  [-v || --verbose     ] Print file contents
--------------------------------------------------------------------------------
                                      NOTES                        
--------------------------------------------------------------------------------
- Required Java Libraries (these are included):

    -OpenHMS Jackcess 3.0.1   
              link: https://jackcess.sourceforge.io/
              
    -Apache Commons Logging 1.2 
              link: https://commons.apache.org/proper/commons-logging/
              
    -Apache Commons Lang 3.9
              link: https://commons.apache.org/proper/commons-lang/
              
    -Apache POI 4.0 (Will 100% be necessary in the future)
              link: https://poi.apache.org/
 
 
- This program includes a sample accdb file to test with, from the following website:
    link: https://www.dur.ac.uk/cis/docs/guides/files/access/
    
- Roadmap:

   ~~- Complete support of all Access data types when writing to Access~~ Done
   - Support for the Jackcess Encryption API for Access Files
   - File splitting and joining as an option
   - Excel Support (legacy and recent)
   - SQL data type support when reading from Access instead of Access data types
   - Better error logging, checking, and messages :: Tentative
   - Enforcing better restrictions on file types
   - GUI support
   - Making an actual in-line ACCDB editor using the GUI (stretch goal)
   
--------------------------------------------------------------------------------
