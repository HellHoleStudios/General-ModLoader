# General ModLoader
By Zzzyt 2018

A general mod loader for all applications

Made with Java SE8

##Usage
###For developers
You should edit the class ModInit (Or extend it) to add your own methods to it.

Then, pack the modified class together with all other classes to a jar file ("ModLoader.jar" in this example) and distribute it to modders.

###For modders
You should first import the distributed jar file as a library.

Then, you can create a class that extends the ModInit class (or any other name the developer gives) and impelment the abstract methods.

You can also add other class as you like. The ModLoader will read and load them all.

You need to add an annotation before your init class(there can only be one such class) like this:

``@Mod(modid="an_identical_name", name = "Any Mod Name You Want!", version = "1.0.0")``

ModLoader will recognize a class with such annotation as an init class(or "entrence class").

<br>
You can use other mods as utils and call their functions.

You can check their versions in the init() method to deal with requirements.

*Little Tip:Some helper methods in ModLoader class may help to check*

##Changelog
**2019/2/14**

-I changed my name from "XiaoMao205" to "Zzzyt" so I updated the package names.

-Deleted the silly github page(index.html).