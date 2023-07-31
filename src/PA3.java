
/*
 * Andrew Fugate - Final PA3 Code - 7/31/2023 @ 1:37pm
 */

//
import java.util.*;
import java.io.*;

public class PA3 {
	/*
	 * global int used for temporary storage; needed to return 2 items from 1
	 * method; this was my work around
	 */
	static int[][] hold = null;

	/* driver function and setting function */
	/*
	 * declares initial variables (ints strings files etc). take in the file input
	 * and stores it. sets source vertex and related info to being the source.
	 *
	 */
	public static void main(String[] args) {
		int VerticesToMake = 0;
		int SourceVertex = 0;
		int Edges = 0;

		/* essentially infinity */
		Integer MAX = Integer.MAX_VALUE;

		/*
		 * garbage variable for comments...when they were still apart of the assignment
		 */
		String temp = null;

		/* reads file */
		File FileIn = new File("cop3503-asn3-input.txt");
		Scanner File = null;

		try {
			File = new Scanner(FileIn);
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		/*
		 * reads file again since you need to have 2 instances of a file; cannot set a
		 * var to the file at 1 certain point as a copy; must use another file
		 */
		File FileIn2 = new File("cop3503-asn3-input.txt");
		Scanner File2 = null;

		try {
			File2 = new Scanner(FileIn2);
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		/* sets vars after file reading */
		VerticesToMake = File.nextInt();
		temp = File.nextLine();
		SourceVertex = File.nextInt();
		temp = File.nextLine();
		Edges = File.nextInt();
		temp = File.nextLine();

		/*
		 * figures out how many edges are needed in each vertex that will be used later
		 * for the creation of edge structures
		 */
		int[][] setup = UnderstandConnections(File, Edges, VerticesToMake);

		/* declares array of vertex data structures */
		VertexStructure[] AllVertices = new VertexStructure[VerticesToMake];

		/* creates number of vertices needed using my vertex structure data type */
		for (int i = 0; i < VerticesToMake; i++) {
			AllVertices[i] = new VertexStructure(i + 1, setup[i][0]);
		}

		/* creates array of edge strcuture data type */
		EdgeStructure[] AllEdges = new EdgeStructure[Edges];

		/* moves edges from input to the data type with the relevant info */
		for (int i = 0; i < Edges; i++) {
			AllEdges[i] = new EdgeStructure(hold[i][0]);
		}

		/*
		 * garbage collection string for when comments were still apart of the
		 * assignment
		 */
		String File2Garbage = null;

		for (int g = 0; g < 3; g++) {
			File2Garbage = File2.nextLine();
		}

		/* skips vertices from file, only caring about weights */
		for (int i = 0; i < Edges; i++) {
			AllEdges[i].VA = AllVertices[File2.nextInt() - 1];
			AllEdges[i].VB = AllVertices[File2.nextInt() - 1];
			String skip = File2.nextLine();
		}

		/* seets vertices edges and the relation */
		for (int i = 0; i < Edges; i++) {
			VertexStructure A = AllEdges[i].VA;
			VertexStructure B = AllEdges[i].VB;

			A.Edges[A.EdgeIndexVar] = AllEdges[i];
			A.EdgeIndexVar++;

			B.Edges[B.EdgeIndexVar] = AllEdges[i];
			B.EdgeIndexVar++;
		}

		/* declares output table; used for file output */
		int[][] OutputTable = new int[VerticesToMake][3];

		/*
		 * sets output table to the intial values - vertex: #-# - cost:
		 * Integer.max_value - from vert: _
		 */
		for (int i = 0; i < VerticesToMake; i++) {
			OutputTable[i][0] = AllVertices[i].label; // vertex:
			OutputTable[i][1] = MAX; // cost:
			OutputTable[i][2] = 0; // from vertex:
		}

		/* used for testing */
		// PrintTable(OutputTable, VerticesToMake);

		// ----------------------------------------------------------------------------------------------------------------------------------------------
		// call the two algos below:
		Bellman(SourceVertex, Edges, VerticesToMake, OutputTable, AllVertices);

		Floyd(VerticesToMake, AllVertices);
	}

	/*
	 * Floyd warshall method takes in the number of vertices in the graph and my
	 * custom vertex structure. The algorithm initializes an array that is NoV x NoV
	 * big and sets the value to infinity (integer max). Then the self loops are set
	 * to 0 which should make a diagonal ( [0][0], [1][1], [2][2], and so on ). Then
	 * the algorithm runs through 3 loops and compares the costs between every
	 * vertex and sees if there is a cheaper way found. Once the algo has ran, the
	 * floyd warshall file is written to.
	 */
	private static void Floyd(int nov, VertexStructure[] verts) {
		int[][] FloydOutput = new int[nov][nov];

		for (int i = 0; i < nov; i++) {
			for (int j = 0; j < nov; j++) {
				FloydOutput[i][j] = Integer.MAX_VALUE;
			}
		}

		for (int i = 0; i < nov; i++) {
			FloydOutput[i][i] = 0;
		}

		for (int i = 0; i < nov; i++) {
			for (int j = 0; j < verts[i].EdgeCount; j++) {
				VertexStructure UseVA = null;
				VertexStructure UseVB = null;

				if (verts[i].label == verts[i].Edges[j].VA.label) {
					UseVA = verts[i].Edges[j].VA;
					UseVB = verts[i].Edges[j].VB;
				}

				else {
					UseVA = verts[i].Edges[j].VB;
					UseVB = verts[i].Edges[j].VA;
				}
				FloydOutput[UseVA.label - 1][UseVB.label - 1] = verts[i].Edges[j].weight;
			}
		}

		// PrintFloyd( FloydOutput, nov );

		for (int i = 0; i < nov; i++) {
			for (int j = 0; j < nov; j++) {
				for (int k = 0; k < nov; k++) {
					if (FloydOutput[j][k] > FloydOutput[j][i] + FloydOutput[i][k]
							&& FloydOutput[j][i] != Integer.MAX_VALUE && FloydOutput[i][k] != Integer.MAX_VALUE) {
						FloydOutput[j][k] = FloydOutput[j][i] + FloydOutput[i][k];
					}
				}
			}
		}

		// PrintFloyd( FloydOutput, nov );

		// cop3503-asn3-output-Fugate-Andrew-fw.txt

		try {
			FileWriter floyd = new FileWriter("cop3503-asn3-output-Fugate-Andrew-fw.txt");

			for (int i = 0; i < nov + 1; i++) {
				if (i == 0) {
					floyd.write("" + nov);
				}

				else {
					floyd.write("\n");

					for (int j = 0; j < nov; j++) {
						floyd.write(FloydOutput[i - 1][j] + " ");
					}
				}
			}
			floyd.close();
		}

		catch (Exception e) {
			e.getStackTrace();
		}
	}

	/*
	 * takes in the output table of floyd and number of vertices and prints
	 * it...mostly used for testing
	 */
	private static void PrintFloyd(int[][] t, int nov) {
		System.out.print("\n\n" + nov);

		for (int i = 0; i < nov; i++) {
			System.out.println();
			for (int j = 0; j < nov; j++) {
				System.out.print(t[i][j] + " ");
			}
		}
	}

	/*
	 * Takes in the starting node, the number of edges and vertices, the initialized
	 * output (set to infinity etc), and my custom vertex structure. Traverses the
	 * vertices in order and sets the costs and from-vertex's appropriately. Makes
	 * no greedy decisions which is where it differs from Dijkstra's. Outputs the
	 * table to the file "cop3503-asn3-output-Fugate-Andrew-bf.txt" by reusing the
	 * write to file method from PA2.
	 */
	private static void Bellman(int SV, int noe, int nov, int[][] OutputTable, VertexStructure[] AllVertices) {
		// System.out.println("bellman");

		OutputTable[SV - 1][1] = 0;
		OutputTable[SV - 1][2] = 0;

		int[] TravelOrder = new int[nov];
		TravelOrder[0] = SV;

		for (int i = 1; i < nov; i++) {
			if (i < SV) {
				TravelOrder[i] = i;
			}

			else {
				TravelOrder[i] = i + 1;
			}
		}

		/*
		 * for( int i = 0; i < nov; i++ ) { System.out.println(TravelOrder[i]); }
		 */

		for (int i = 0; i < nov - 1; i++) {
			for (int j = 0; j < AllVertices[TravelOrder[i] - 1].EdgeCount; j++) {
				// PrintTable( OutputTable, nov );

				VertexStructure UseVA = null;
				VertexStructure UseVB = null;

				if (AllVertices[TravelOrder[i] - 1].Edges[j].VA == AllVertices[TravelOrder[i] - 1]) {
					UseVA = AllVertices[TravelOrder[i] - 1].Edges[j].VA;
					UseVB = AllVertices[TravelOrder[i] - 1].Edges[j].VB;
				}

				else {
					UseVA = AllVertices[TravelOrder[i] - 1].Edges[j].VB;
					UseVB = AllVertices[TravelOrder[i] - 1].Edges[j].VA;
				}

				if (OutputTable[UseVA.label - 1][1] == Integer.MAX_VALUE) {
					continue;
				}

				else {
					if (UseVA.Edges[j].weight + OutputTable[UseVA.label - 1][1] < OutputTable[UseVB.label - 1][1]) {
						OutputTable[UseVB.label - 1][1] = UseVA.Edges[j].weight + OutputTable[UseVA.label - 1][1];
						OutputTable[UseVB.label - 1][2] = UseVA.label;
					}
				}
			}
		}

		for (int i = 0; i < nov; i++) {
			// PrintTable( OutputTable, nov );

			VertexStructure UseVA = null;
			VertexStructure UseVB = null;

			for (int j = 0; j < AllVertices[i].EdgeCount; j++) {
				if (AllVertices[i].Edges[j].VA == AllVertices[i]) {
					UseVA = AllVertices[i].Edges[j].VA;
					UseVB = AllVertices[i].Edges[j].VB;
				}

				else {
					UseVA = AllVertices[i].Edges[j].VB;
					UseVB = AllVertices[i].Edges[j].VA;
				}

				if (UseVA.Edges[j].weight + OutputTable[UseVA.label - 1][1] < OutputTable[UseVB.label - 1][1]) {
					OutputTable[UseVB.label - 1][1] = UseVA.Edges[j].weight + OutputTable[UseVA.label - 1][1];
					OutputTable[UseVB.label - 1][2] = UseVA.label;
				}
			}
		}
		WriteToFile(OutputTable, nov);
	}

	/*
	 * takes in the arrays of vertices and edges as well as how many vertices there
	 * are and returns the shortest path. It does so by test traversing and storing
	 * the lowest path it found
	 */
	private static int SearchForShortestPath(VertexStructure[] verts, int[][] out, int nov) {
		int CSP = Integer.MAX_VALUE;
		int NTR = -1;

		for (int i = 0; i < nov; i++) {
			if (verts[i].visited == false) {
				if (out[verts[i].label - 1][1] < CSP && out[verts[i].label - 1][1] >= 0) {
					// System.out.printf("\nShowing vertex %d has the lowest path of %d on check
					// %d\nvertex %d's cost is %d", verts[i].label, out[ verts[i].label - 1 ][1],
					// i+1,verts[i].label, out[verts[i].label-1][1]);
					CSP = out[verts[i].label - 1][1];
					NTR = verts[i].label;
				}
			}
		}
		return NTR;
	}

	/*
	 * takes in the current output table (should be the expected output) and writes
	 * it
	 */
	private static void WriteToFile(int[][] OutputTable, int VerticesToMake) {
		try {
			FileWriter results = new FileWriter("cop3503-asn3-output-Fugate-Andrew-bf.txt");

			for (int i = 0; i < VerticesToMake + 1; i++) {
				if (i == 0) {
					results.write("" + VerticesToMake + "\n");
				}

				else {
					results.write(
							OutputTable[i - 1][0] + " " + OutputTable[i - 1][1] + " " + OutputTable[i - 1][2] + "\n");
				}
			}

			results.close();
		}

		catch (Exception e) {
			e.getStackTrace();
		}
	}

	/*
	 * takes in the output table and number of vertices and prints it...mostly used
	 * for testing
	 */
	private static void PrintTable(int[][] out, int nov) {

		System.out.println();
		System.out.printf("\nVertex: Cost: From:");

		for (int i = 0; i < nov; i++) {
			System.out.printf("\n%d %d %d", out[i][0], out[i][1], out[i][2]);
		}
	}

	/*
	 * takes in the file and number of edges and vertices and assigns the
	 * appropraite values that each should have in its structure
	 */
	private static int[][] UnderstandConnections(Scanner file, int noe, int nov) {
		int[][] setup = new int[nov][1];
		int[][] EdgeWeights = new int[noe][1];

		for (int i = 0; i < noe; i++) {
			int temp = file.nextInt();
			int temp2 = file.nextInt();
			int temp3 = file.nextInt();

			EdgeWeights[i][0] = temp3;

			setup[temp - 1][0]++;
			setup[temp2 - 1][0]++;
		}

		hold = EdgeWeights;

		return setup;
	}

	/* structure of the vertices */
	public static class VertexStructure {

		int label = 0;
		boolean visited = false;

		int EdgeIndexVar = 0;

		int EdgeCount = 0;
		EdgeStructure[] Edges = null;

		public VertexStructure(int label, int edgeCount) {
			super();
			this.label = label;
			EdgeCount = edgeCount;

			Edges = new EdgeStructure[edgeCount];
		}
	}

	/* structure of the edges */
	public static class EdgeStructure {
		// VA VB as in V1 V2 for Vertex1 and Vertex2 but avoiding too many numbers so
		// using alpha
		VertexStructure VA = null;
		VertexStructure VB = null;

		int weight = -1;

		public EdgeStructure(VertexStructure vA, VertexStructure vB) {
			super();
			VA = vA;
			VB = vB;
		}

		public EdgeStructure(int weight) {
			this.weight = weight;
		}

	}

}

//

//