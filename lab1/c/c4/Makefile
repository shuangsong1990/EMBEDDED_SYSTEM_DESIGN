SCC = scc
CMP = diff -s
SCCOPT  = -vv -ww -g


GOLDFILE = golden.pgm
INFILE =   input_small.pgm
OUTFILE =  output_edge.pgm


all: design

design: SusanThin.sc EdgeDraw.sc DetectEdges.sc get_image.sc put_image.sc susan.sc read_image.sc write_image.sc design.sc
	$(SCC) design $(SCCOPT)

test:
	./design
	$(CMP) $(OUTFILE) $(GOLDFILE)

clean:
	-rm -f *~ *.o *.cc *.h
	-rm -f *.si design
