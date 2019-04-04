package org.program.transformation;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CastingTest {
	@Test
	public void testCasting() {
		String expected = "ascii 0 character \u0000\n" +
				"ascii 1 character \u0001\n" +
				"ascii 2 character \u0002\n" +
				"ascii 3 character \u0003\n" +
				"ascii 4 character \u0004\n" +
				"ascii 5 character \u0005\n" +
				"ascii 6 character \u0006\n" +
				"ascii 7 character \u0007\n" +
				"ascii 8 character \b\n" +
				"ascii 9 character \t\n" +
				"ascii 10 character \n" +
				"\n" +
				"ascii 11 character \u000B\n" +
				"ascii 12 character \f\n" +
				"ascii 13 character \n" +
				"ascii 14 character \u000E\n" +
				"ascii 15 character \u000F\n" +
				"ascii 16 character \u0010\n" +
				"ascii 17 character \u0011\n" +
				"ascii 18 character \u0012\n" +
				"ascii 19 character \u0013\n" +
				"ascii 20 character \u0014\n" +
				"ascii 21 character \u0015\n" +
				"ascii 22 character \u0016\n" +
				"ascii 23 character \u0017\n" +
				"ascii 24 character \u0018\n" +
				"ascii 25 character \u0019\n" +
				"ascii 26 character \u001A\n" +
				"ascii 27 character \u001B\n" +
				"ascii 28 character \u001C\n" +
				"ascii 29 character \u001D\n" +
				"ascii 30 character \u001E\n" +
				"ascii 31 character \u001F\n" +
				"ascii 32 character  \n" +
				"ascii 33 character !\n" +
				"ascii 34 character \"\n" +
				"ascii 35 character #\n" +
				"ascii 36 character $\n" +
				"ascii 37 character %\n" +
				"ascii 38 character &\n" +
				"ascii 39 character '\n" +
				"ascii 40 character (\n" +
				"ascii 41 character )\n" +
				"ascii 42 character *\n" +
				"ascii 43 character +\n" +
				"ascii 44 character ,\n" +
				"ascii 45 character -\n" +
				"ascii 46 character .\n" +
				"ascii 47 character /\n" +
				"ascii 48 character 0\n" +
				"ascii 49 character 1\n" +
				"ascii 50 character 2\n" +
				"ascii 51 character 3\n" +
				"ascii 52 character 4\n" +
				"ascii 53 character 5\n" +
				"ascii 54 character 6\n" +
				"ascii 55 character 7\n" +
				"ascii 56 character 8\n" +
				"ascii 57 character 9\n" +
				"ascii 58 character :\n" +
				"ascii 59 character ;\n" +
				"ascii 60 character <\n" +
				"ascii 61 character =\n" +
				"ascii 62 character >\n" +
				"ascii 63 character ?\n" +
				"ascii 64 character @\n" +
				"ascii 65 character A\n" +
				"ascii 66 character B\n" +
				"ascii 67 character C\n" +
				"ascii 68 character D\n" +
				"ascii 69 character E\n" +
				"ascii 70 character F\n" +
				"ascii 71 character G\n" +
				"ascii 72 character H\n" +
				"ascii 73 character I\n" +
				"ascii 74 character J\n" +
				"ascii 75 character K\n" +
				"ascii 76 character L\n" +
				"ascii 77 character M\n" +
				"ascii 78 character N\n" +
				"ascii 79 character O\n" +
				"ascii 80 character P\n" +
				"ascii 81 character Q\n" +
				"ascii 82 character R\n" +
				"ascii 83 character S\n" +
				"ascii 84 character T\n" +
				"ascii 85 character U\n" +
				"ascii 86 character V\n" +
				"ascii 87 character W\n" +
				"ascii 88 character X\n" +
				"ascii 89 character Y\n" +
				"ascii 90 character Z\n" +
				"ascii 91 character [\n" +
				"ascii 92 character \\\n" +
				"ascii 93 character ]\n" +
				"ascii 94 character ^\n" +
				"ascii 95 character _\n" +
				"ascii 96 character `\n" +
				"ascii 97 character a\n" +
				"ascii 98 character b\n" +
				"ascii 99 character c\n" +
				"ascii 100 character d\n" +
				"ascii 101 character e\n" +
				"ascii 102 character f\n" +
				"ascii 103 character g\n" +
				"ascii 104 character h\n" +
				"ascii 105 character i\n" +
				"ascii 106 character j\n" +
				"ascii 107 character k\n" +
				"ascii 108 character l\n" +
				"ascii 109 character m\n" +
				"ascii 110 character n\n" +
				"ascii 111 character o\n" +
				"ascii 112 character p\n" +
				"ascii 113 character q\n" +
				"ascii 114 character r\n" +
				"ascii 115 character s\n" +
				"ascii 116 character t\n" +
				"ascii 117 character u\n" +
				"ascii 118 character v\n" +
				"ascii 119 character w\n" +
				"ascii 120 character x\n" +
				"ascii 121 character y\n" +
				"ascii 122 character z\n" +
				"ascii 123 character {\n" +
				"ascii 124 character |\n" +
				"ascii 125 character }\n" +
				"ascii 126 character ~\n" +
				"ascii 127 character \u007F\n";
		assertTrue(TestUtil.testMain("org.program.transformation.Casting",new String[]{},expected));
	}
}
