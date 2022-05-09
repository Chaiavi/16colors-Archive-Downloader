# 16colors-Archive-Downloader
Downloader for the whole [Sixteen Colors Archive](https://github.com/sixteencolors/sixteencolors-archive)
  
The need for this downloader comes from the fact that the Sixteen Colors (ANSI) Archive is huge (more than 4.5gb).  
thus is complicated to download in some systems.  
  
This downloader is a robust downloader which has "Resume" functionality and nice logs :-)  so the user will be able to download all of the archive with a fail safe mechanism so if the download fails he will be able to resume the download at a later stage.  
  
  
There is a (hidden) option to download specific years of ANSI packs by defining the specific years in the command line as arguments  
**Example: Java -jar 16c-Archive-Downloader-1.0.jar 1994 2001 2007**  
  
The above will download only the archives of 1994, 2001 and 2007 while skipping the rest.
