# JeXiMeL

JeXiMeL is a small, light-weight XML parser for Java. There are many like it but this one is mine. It supports the basic needs that an XML parser should have. You can read and write files from XML documents, or create a document in code and write it to file 
later. Use it if you wish.

## Features

* Very small library, which can read and write files very quickly.
* Can read XML formatted files from an InputStream.
* Can write XML formatted files to an OutputStream.
* Supports XML declarations.
* Supports multiple root-level elements.
* Supports nested elements, with attributes and text nodes.
* Formats everything in a simple tree, with functions like `.getChild("MyElement").getAttribute("MyAttrib")`.
* Fairly robust error handling it seems. Can continue where fatal errors often halt. If this is reliable has yet to be seen.

It does not support XML entities, because I didn't need it for myself and so I haven't looked into how they work.
And although it works with all XML files I have tested, it probably doesn't adhere completely to the XML specification.

## Usage

After importing `XMLParser`, you can call the static method `read` or `write` to access files on the disk. If you want to construct an XML document programmatically, you can create a new `Document` instance and start adding `Element`s. Functions are documented 
and fairly straight forward. Functions include things like `getAttributes` and `getChildren` and `getName` and `getText`. You get the idea.

To be more in-depth, here's how you can create an XML file programmatically:
```java
// Create a new, empty XML document.
Document document = new Document();
// Adding a child element to the document.
document.addChild(new Element("FirstChild"));
// A parent element can also be assigned when creating a new element, 
// if you want to assign more information to it.
Element anElement = new Element(document.getChild("FirstChild"), "SecondChild");
// Attributes can be added
anElement.addAttribute("FirstAttrib", "true");
// Orphaned elements aren't included in the document, so be careful
Element orphan = new Element("Orphan");
// But you can edit the parent later
orphan.setParent(anElement);
orphan.setName("Adopted");
// Text nodes can be set
orphan.setText("This element has a text node.");
// Setting declaration information is also possible
document.setEncoding("UTF-16");

// Okay, let's write this to a file.
File outputFile = new File("path/to/file");
XMLParser.write(document, new FileOutputStream(outputFile));
```

Here we have the output of the file.
```xml
<?xml encoding="UTF-16" ?>
<FirstChild>
	<SecondChild FirstAttrib="true">
		<Adopted>This element has a text node.</Adopted>
	</SecondChild>
</FirstChild>
```

The reverse process can be used for reading XML files.

```java
File inputFile = new File("path/to/file");
// Load the file into this document.
Document document = XMLParser.read(new FileInputStream(inputFile));
// Now to get some data...
Element secondChild = document.getChild("FirstChild").getChild("SecondChild");
String firstAttrib = secondChild.getAttribute("FirstAttrib");
String text = secondChild.getChild("Adopted").getText();
// If you have several children somewhere, you can loop through them
for(Element child : secondChild.getChildren()) {...}
```

## Other

Why did I make this when there are already well established XML parsers for Java, even built-in? Maybe I'm stupid. I just felt like it. I guess I like doing things my way, and this was perhaps another chance for me to learn more through experience. 
