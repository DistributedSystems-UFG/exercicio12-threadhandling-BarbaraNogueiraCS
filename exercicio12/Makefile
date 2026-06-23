SRC_DIR=src
OUT_DIR=out
MAIN_CLASS=SimpleThreads
ARGS=5

.PHONY: compile run clean

compile:
	mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) $(SRC_DIR)/$(MAIN_CLASS).java

run: compile
	java -cp $(OUT_DIR) $(MAIN_CLASS) $(ARGS)

clean:
	rm -rf $(OUT_DIR)
