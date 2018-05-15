JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
        CanvasView.java\
        Drawable.java \
        Main.java \
        MainView.java \
        Model.java \
        Observer.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class

run:	classes
	java Main	
