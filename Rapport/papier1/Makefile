##
## A helper Makefile to build easychair-based articles
## under *nix environments with GNU make.
##
## Modify according to your needs.
##
## Author: Serguei Mokhov
## $Id: Makefile,v 1.8 2009/03/21 02:08:16 mokhov Exp $
##

#
# Settings
#

#PDFLATEXFLAGS = --interaction=batchmode --file-line-error-style
PDFLATEXFLAGS = --interaction=batchmode

DELIVERABLE = easychair
SUBMISSIONDIR = easychair-submission


#
# Build
#

all: letter a4
#all: $(DELIVERABLE).pdf

a4: $(DELIVERABLE)-a4.pdf
letter: $(DELIVERABLE)-letter.pdf

$(DELIVERABLE).pdf: $(DELIVERABLE).tex $(DELIVERABLE).bib $(DELIVERABLE).cls Makefile
	@echo "Compiling *.tex files..."
	pdflatex $(PDFLATEXFLAGS) $(DELIVERABLE)
	@echo "Compiling bibliography..."
	bibtex $(DELIVERABLE)
	@echo "Updating cross-references..."
	pdflatex $(PDFLATEXFLAGS) $(DELIVERABLE)
	pdflatex $(PDFLATEXFLAGS) $(DELIVERABLE)

$(DELIVERABLE)-letter.pdf: $(DELIVERABLE).pdf
	cp $(DELIVERABLE).tex $(DELIVERABLE)-letter.tex
	cp $(DELIVERABLE).pdf $(DELIVERABLE)-letter.pdf
	@echo "US Letter build is done."

$(DELIVERABLE)-a4.pdf: $(DELIVERABLE).pdf
	@echo "Prepping A4 .tex file..."
	cat $(DELIVERABLE).tex | sed 's/\\documentclass{/\\documentclass[a4paper]{/g' > $(DELIVERABLE)-a4.tex
	@echo "Compiling *.tex files..."
	pdflatex $(PDFLATEXFLAGS) $(DELIVERABLE)-a4
	@echo "Compiling bibliography..."
	bibtex $(DELIVERABLE)-a4
	@echo "Updating cross-references..."
	pdflatex $(PDFLATEXFLAGS) $(DELIVERABLE)-a4
	pdflatex $(PDFLATEXFLAGS) $(DELIVERABLE)-a4
	@echo "A4 build is done."

submission: all
	mkdir -p $(SUBMISSIONDIR)
	cp Makefile *.tex *.bib *.pdf *.png *.jpg $(SUBMISSIONDIR)
	make -C $(SUBMISSIONDIR) clean
	tar cvf $(SUBMISSIONDIR).tar $(SUBMISSIONDIR)
	gzip --best $(SUBMISSIONDIR).tar

zip: all
	zip -9 $(DELIVERABLE).zip \
		$(DELIVERABLE).cls \
		$(DELIVERABLE).tex $(DELIVERABLE).bib \
		$(DELIVERABLE)-*.pdf *EC.pdf *.jpg \
		Makefile $(DELIVERABLE).tcp

#
# Clean up
#

clean:
	rm -f *~ *.log *.out *.dvi *.aux *.toc \
          *.lot *.lof *.idx *.bbl *.blg *.ilg *.ind

maintainer-clean: clean
	rm -f $(DELIVERABLE).pdf $(DELIVERABLE)-a4* $(DELIVERABLE)-letter*

# EOF
