CC = gcc
CMP = diff -s
RM  = rm -rf

GOLDFILE = golden.pgm
INFILE =   input_small.pgm    
OUTFILE =  output_edge.pgm



#susan: susan.c Makefile
susan_check: susan_check.c Makefile
	$(CC) -static -O4 -o susan_check susan_check.c -lm 

test:
	./susan_check $(INFILE) $(OUTFILE)
	$(CMP) $(OUTFILE) $(GOLDFILE)

clean:
	$(RM) susan_check $(OUTFILE)
