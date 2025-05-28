JAVAC = javac -cp "lib/sqlite-jdbc.jar:lib/jcalendar-1.4.jar:."
JAVA = java -cp "bin:lib/sqlite-jdbc.jar:lib/jcalendar-1.4.jar:."
JAR = jar

SRC_DIR = src
BIN_DIR = bin
TEMP_DIR = temp

SOURCES = $(shell find $(SRC_DIR) -type f -name "*.java")
MAIN_CLASS = main.Main
TARGET = PersonalOrganizerApp.jar

.PHONY: all resources jar clean run macapp

all: resources $(SOURCES)
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(SOURCES)

resources:
	@mkdir -p $(BIN_DIR)
	cp -R $(SRC_DIR)/resources $(BIN_DIR)/

jar: all
	@mkdir -p $(TEMP_DIR)
	cd $(TEMP_DIR) && $(JAR) xf ../lib/sqlite-jdbc-3.49.1.0.jar && $(JAR) xf ../lib/jcalendar-1.4.jar
	$(JAR) cvfe $(TARGET) $(MAIN_CLASS) -C $(BIN_DIR) . -C $(TEMP_DIR) .
	@rm -rf $(TEMP_DIR)

clean:
	rm -rf $(BIN_DIR) $(TARGET)

run: jar
	$(JAVA) -jar $(TARGET)

# Target to build a Mac app bundle using jpackage (requires JDK 14+ and icon.icns in project root)
macapp: jar
	jpackage --name "Personal Organizer App" --app-version 1.0 --input . --main-jar $(TARGET) --main-class $(MAIN_CLASS) --icon icon.icns --type app-image
