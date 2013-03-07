#
# makefile for svm_light 
#
# Thorsten Joachims, 2002
#
# Thanks to Ralf Herbrich for the initial version.
#

all:
	@echo ""
	@echo "USAGE make [win|linux|osx]"
	@echo ""

linux:
	make -f Makefile-linux

win:
	make -f Makefile-windows

osx:
	make -f Makefile-osx
    
tidy:
	rm -f *.o 
	rm -f donlp2/*.o
	rm -f donlp2_c/*.o
	rm -f pr_loqo/*.o

clean:	tidy
	rm -f linux-svm_learn*
	rm -f linux-svm_classify*
	rm -f libsvmlight.*
	rm -f svmlight.dll
	rm -f win-svm_learn*
	rm -f win-svm_classify*

