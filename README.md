# browserless-compiler

Maven plugin for browser based Less-compilation.

Uses Selenium HtmlUnitDriver to execute less.js as a web browser. This actually turns out to be faster and less error-prone than other maven plugins for LESS-compilation, such as [lesscss-maven-plugin](https://github.com/marceloverdijk/lesscss-maven-plugin).


## Example usage

```xml
<plugin>
    <groupId>no.uio.weblibs</groupId>
    <artifactId>browserless-compiler</artifactId>
    <version>1.0-SNAPSHOT</version>
    <configuration>
        <lessJs>${project.basedir}/src/main/webapp/lib/less.js</lessJs>
        <sourceDirectory>${project.basedir}/src/main/webapp</sourceDirectory>
        <outputDirectory>${project.build.directory}/${project.build.finalName}</outputDirectory>
        <compress>true</compress>
        <includes>
            <include>css/style.less</include>
            <include>css/style-responsive.less</include>
        </includes>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Configuration

The following configuration options are available:

#### lessJs (file path)
Location of [less.js](http://lesscss.org/#download-options) in your project.

#### sourceDirectory (folder path)
Source directory of your web resources. This will also be used as a base for relativization of CSS URIs.

### outputDirectory (folder path)
Target folder of compiled CSS-files. The files will be placed in the same folder structure relative to **sourceDirectory**.

### includes (fileset patterns)
List of less-fileset patterns to include relative to **sourceDirectory**.

### excludes (fileset patterns)
List of less-fileset patterns to exclude for compilation.

#### compress (boolean)
Whether to enable less.js compression. See "Compress" under [less.js options](http://lesscss.org/usage/#command-line-usage-options). Default false.

### absolutePaths (boolean)
Whether to use absolute paths in compiled CSS. Not recommended for deployment. Default false.

### encoding (string)
File encoding of compiled CSS. Default UTF-8.

### skip (boolean)
Whether to skip compilation.
