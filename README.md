# Kingdoms
The plugin for managing Kingdoms within the Morphics network

Authors: Dallen, Icy, WezD

[Kingdom design/development folder](https://goo.gl/n6xMft)

# A quick overview
=
Kingdoms runs a little differently than most plugins because it uses large
amounts of reflection to take care of basic tasks. This will give quick examples
of how to create or add basic functions to the plugin. The most important part of
this is that you must ALWAYS use LogUtil.printDebug(msg) to print any debug 
information you require. This function allows us to turn off your debug info when
we install it on production.

### Adding a command
Adding a command in Kingdoms is not much different from normal, add your command
to the plugin.yml and register it in the Main onServerLoad runnable to be handled
by one of the handlers in the Commands folder (a command that deals with moderation
should most likely be added to the ModerationCommand.java if possible

### Adding an Event Handler
Adding an event handler in Kingdoms is a little different from most plugins, to 
register a listener, all you need to do is create a class the implements listener,
has a constructor with no arguments (this is default if you define no constructor),
and has at least one method with the @EventHandler annotation. The plugin will 
register you handler automatically. Listeners should ideally be put in the Handlers
folder unless they are more fitting elsewhere.

### Adding a plot class
Adding a plot class is a bit more involved. First make sure you class extends Plot,
is located in Kingdom/Structure/Types, and has a constructor that accepts Plot 
as the only argument and calls super(<plot>) in the first line. Next you will 
need to add you plot to the menu, you can find the menu in Plot.java under 
defEditMenu(). If you want your plot to have a custom icon you can also add a
KingdomMaterial for it (that class is under Overrides).

#### Making your plot super fancy
Now that you have a plot class you can add variables that will be stored with it
as well as give your plot a BuildingVault. This will allow you to handle all player
interaction within your plot through the interact(PlayerInteractEvent event) method
in you class. You will need a variable named Storage that has the annotations:
@Getter @Setter @SaveData. This will tell the plugin to save that data along with 
the plot. You can do this for many different types of data, including: Arrays, Maps,
Locations, Polygons, Ellipses, BuildingVaults, and many raw data types as well.

### Adding save data
To add save data direct yourself to the Storage folder where you will find the 
DataLoadHelper. This class helps load and save all kingdoms data. You will also
find many classes that help convert java objects to Json and back. If you want to
create a new type of object that needs saving you will need to write the save code
yourself, DBmanager has all the methods you should need for saving a loading your
data. You will also need a way to convert all your data into base java types that
Json can understand and parse or you will have a real mess on your hands. I reiterate,
YOU DO NOT NEED TO WRITE SAVE AND LOAD CODE FOR PLOT CLASSES unless you need some
type of data that is not supported

