package dev.marcusxavier.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }

        String outputDir = args[0];
        defineAst(outputDir, "Expr", Arrays.asList(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
        ));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

        writer.println("package dev.marcusxavier.lox;");
        writer.println();

        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");
        // Visitor interface
        defineVisitor(writer, baseName, types); 

        // WRITE SUBCLASS LOOP
        for (String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }
      
        // The base accept() method.
        writer.println();
        writer.println("\tabstract <R> R accept(Visitor<R> visitor);");
        // END SUBCLASS LOOP
        writer.println("}");
        writer.close();
    }

    // HACK: HERE, THE VISITOR PATTERN IS USED TO INJECT BEHAVIOUR ON OUR MULTIPLE TYPE CLASSES. THIS ALLOWS US TO SOMETIMES INJECT PARSER CODE ON IT
    // AND SOMETIMES INJECT INTERPRETER CODE ON IT. EACH CLASS WILL BE A NODE ON OUR SYNTAX TREE. SO WE ARE INJECTING BEHAVIOUR ON OUR NODES.
    // NOT ONLY BEHAVIOUR, BUT TYPED BEHAVIOUR THAT CAN KNOWS HOW TO WORK WITH A SPECIFIC CLASS, HOW TO ACCESS ITS ATTRIBUTES, ETC.
    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("\tinterface Visitor<R> {"); 
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(String.format("\t\tR visit%s%s(%s %s);", typeName, baseName, typeName, baseName.toLowerCase())); 
        }
        writer.println("\t}"); 
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        // Open
        writer.println(String.format("\tstatic class %s extends %s {", className, baseName));
            // Constructor
            writer.println(String.format("\t\t%s(%s) {", className, fieldList));
            String[] fields = fieldList.split(", ");
                for (String field : fields)  {
                   String name = field.split(" ")[1];
                   writer.println(String.format("\t\t\tthis.%s = %s;", name, name));
                }
            writer.println("\t\t}");
                
            // Visitor pattern.
            writer.println();
            writer.println("\t\t@Override");
            writer.println("\t\t<R> R accept(Visitor<R> visitor) {");
            writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
            writer.println("\t\t}"); 
            
            // Fields
            writer.println();
            for (String field: fields) {
                writer.println(String.format("\t\tfinal %s;", field));
            }
        // Close
        writer.println("\t}");
        writer.println();
    }
}
