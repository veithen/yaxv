import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

public class mkresources {
    public static void main(String[] args) throws IOException {
        ResourceBundle messages = ResourceBundle.getBundle("net.sf.yaxv.messages");
        PrintWriter out = new PrintWriter(new FileWriter("build/gen-src/net/sf/yaxv/Resources.java"));
        out.println("package net.sf.yaxv;");
        out.println();
        out.println("import java.util.ResourceBundle;");
        out.println();
        out.println("public interface Resources {");
        out.println("\tfinal static ResourceBundle MESSAGES = ResourceBundle.getBundle(\"net.sf.yaxv.messages\");");
        out.println();
        StringBuffer buff = new StringBuffer();
        Set sortedKeys = new TreeSet();
        for (Enumeration keys = messages.getKeys(); keys.hasMoreElements(); ) {
            sortedKeys.add(keys.nextElement());
        }
        for (Iterator it = sortedKeys.iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            buff.setLength(0);
            for (int i=0; i<key.length(); i++) {
                char c = key.charAt(i);
                if (c == '.') {
                    buff.append('_');
                } else if ('A' <= c && c <= 'Z') {
                    buff.append(c);
                } else if ('a' <= c && c <= 'z') {
                    buff.append((char)(c-'a'+'A'));
                }
            }
            out.println("\tfinal static String " + buff + " = \"" + key + "\";");
        }
        out.println("}");
        out.flush();
    }
}
