CC = gcc
CMP = diff -s
RM  = rm -rf

GOLDFILE = golden.pgm
INFILE =   input_small.pgm    
OUTFILE =  output_edge.pgm



#susan: susan.c Makefile
new_susan: new_susan.c Makefile
	$(CC) -static -O4 -o new_susan new_susan.c -lm 

test:
	./new_susan $(INFILE) $(OUTFILE)
	$(CMP) $(OUTFILE) $(GOLDFILE)

clean:
	$(RM) new_susan $(OUTFILE)
