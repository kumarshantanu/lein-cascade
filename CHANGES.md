# Changes and TODO


## 0.1.1 / 2013-Sep-18

* Special treatment to symbols:
   * `%&` is treated same as `"$@"` in bash and `%*` in Windows batch files
* Verbose logging of what is going on


## 0.1.0 / 2013-Sep-18

* Dependent tasks declaration and resolution
* Special treatment to symbols:
   * `%` and `%integer` symbols are looked up as command-line arguments
   * All other symbols are looked up as environment variables

