SCC = scc
CMP = diff -s
SCCOPT  = -vv -ww -g


GOLDFILE = golden.pgm
INFILE =   input_small.pgm
OUTFILE =  output_edge.pgm


all: susan_edge_detector

susan_edge_detector: susan_edge_detector.sc SusanThin.sc EdgeDraw.sc DetectEdges.sc get_image.sc put_image.sc susan_edge_detector.sc
	$(SCC) susan_edge_detector $(SCCOPT)

test:
	./susan_edge_detector
	$(CMP) $(OUTFILE) $(GOLDFILE)

clean:
	-rm -f *~ *.o *.cc *.h
	-rm -f *.si susan_edge_detector
