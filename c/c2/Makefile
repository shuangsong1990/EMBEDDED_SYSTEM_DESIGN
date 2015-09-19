SCC = scc
CMP = diff -s
SCCOPT  = -vv -ww -g


GOLDFILE = golden.pgm
INFILE =   input_small.pgm
OUTFILE =  output_edge.pgm


all: susan

susan: susan.sc SusanThin.sc EdgeDraw.sc DetectEdges.sc get_image.sc put_image.sc susan.sc
	$(SCC) susan $(SCCOPT)

test:
	./susan
	$(CMP) $(OUTFILE) $(GOLDFILE)

clean:
	-rm -f *~ *.o *.cc *.h
	-rm -f *.si susan
