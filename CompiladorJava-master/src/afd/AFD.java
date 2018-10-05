package afd;

import jdk.nashorn.internal.runtime.regexp.joni.constants.AsmConstants;
import org.antlr.v4.runtime.Token;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AFD {
    private GraphViz gv;
    private String extensaoArquivo;
    private String tipoDeRepresentacao;
    private List<Token> listaTokens;
    private int contador = 1;
    private ArrayList<Vertice> listaVertices = new ArrayList<>();

    public AFD(List<Token> listaTokens, String extensaoArquivo) {
        gv = new GraphViz();
        this.extensaoArquivo = extensaoArquivo;
        tipoDeRepresentacao = "dot";
        this.listaTokens = listaTokens;
        construirAFD();
        criarArquivo();
    }

    private void construirAFD() {
        gv.addln(gv.start_graph());


        Vertice init = new Vertice(0);
        Automato a = new Automato(init);
        listaVertices.add(init);
        ArrayList<String> listaStrings = new ArrayList<>();
        for (int i = 0; i < (listaTokens.size() - 1); i++) {
            switch (listaTokens.get(i).getType()){
                case 51:
                    String sAux = "q" + 0 + " ->" + "id1" + " [label=\"" + "(('a'..'z') | ('A'..'Z') | '_' | '$')" + "\"];";
                    sAux+="id1 [shape=doublecircle]\n";
                    sAux+= "id1" + " ->" + "id1" + " [label=\"" + "(('a'..'z') | ('A'..'Z') | ('0'..'9') | '_' | '$')" + "\"];";
                    if (!(listaStrings.contains(sAux))){
                        listaStrings.add(sAux);
                    }

                    break;
                case 87:
                    System.out.println("entro");

                    sAux = "dn1 [shape=doublecircle]\n;";
                    sAux += "q0" + " ->" + "dn1" + " [label=\"" + "'0'" + "\"];";
                    sAux += "dn1" + " ->" + "dn3" + " [label=\"" + "('l' | 'L')" + "\"];";

                    sAux +="dn2 [shape=doublecircle]\n;";
                    sAux += "q0" + " ->" + "dn2" + " [label=\"" + "NonZeroDigit" + "\"];";
                    sAux += "dn2" + " ->" + "dn2" + " [label=\"" + "'Digit'" + "\"];";
                    sAux += "dn2" + " ->" + "dn3" + " [label=\"" + "('l' | 'L')" + "\"];";
                    sAux += "dn3 [shape=doublecircle]\n;";


                    if (!(listaStrings.contains(sAux))){
                        listaStrings.add(sAux);
                    }
                    break;
                case 88:
                    sAux ="on1 [shape=doublecircle]\n;";
                    sAux +="on2 [shape=doublecircle]\n;";
                    sAux += "q0" + " ->" + "on1" + " [label=\"" + "'0'" + "\"];";
                    sAux += "on1" + " ->" + "on1" + " [label=\"" + "OctalDigit" + "\"];";
                    sAux += "on1" + " ->" + "on2" + " [label=\"" + "('l' | 'L')" + "\"];";
                    if (!(listaStrings.contains(sAux))){
                        listaStrings.add(sAux);
                    }
                    break;
                case 89:
                    sAux = "hx1 [shape=doublecircle];\n";
                    sAux +="hx2 [shape=doublecircle];\n";
                    sAux += "q0" + "->" + "hx1" + " [label=\"" + "('0x' | '0X')" + "\"];";
                    sAux += "hx1" + "->" + "hx1" + " [label=\"" + "HexDigit" + "\"];";
                    sAux += "hx1" + "->" + "hx2" + " [label=\"" + "('l' | 'L')" + "\"];";

                    if (!(listaStrings.contains(sAux))){
                        listaStrings.add(sAux);
                    }
                    break;
                case 99:

                    sAux = "q0" + "->" + "str1" + " [label=\"" + "string" + "\"];";
                    sAux += "str1" + "->" + "str1" + " [label=\"" + "*" + "\"];";
                    sAux += "str2 [shape=doublecircle];\n";
                    sAux += "str1" + "->" + "str2" + " [label=\"" + "string" + "\"];";
                    System.out.println(sAux);
                    if (!(listaStrings.contains(sAux))){
                        listaStrings.add(sAux);
                    }
                    break;

                default:
                    String lex = listaTokens.get(i).getText();
                    Vertice atual = init;
                    for (int j = 0; j < lex.length(); j++) {

                        boolean achou = false;
                        for (int k = 0; k < atual.getAl().size(); k++) {
                            //System.out.println(atual.al.get(k).transicao + "     " + String.valueOf(lex.charAt(j) ));
                            char q = atual.getAl().get(k).getTransicao();
                            char b = lex.charAt(j);
                            //System.out.println(a + " - " + b);
                            if (q == b) {
                                //System.out.println("olar");
                                atual = atual.getAl().get(k).getDestino();
                                achou = true;
                                break;
                            }
                        }
                        if (!achou) {
                            Vertice aux = new Vertice(contador);
                            contador++;
                            listaVertices.add(aux);
                            String as;
                            //System.out.print(lex.charAt(j));
                            atual.getAl().add(new Transicao(lex.charAt(j), aux));
                            atual = aux;
                        }
                    }
                    break;
            }

        }
        for (Vertice v : listaVertices){
            for (int i = 0; i < v.getAl().size(); i++){
                gv.addln("q" + v.getNumC() + " -> q" + v.getAl().get(i).getDestino().getNumC() + " [label=\"" +v.getAl().get(i).getTransicao() + "\"];");
            }

        }
        for (String str : listaStrings){
            gv.addln(str);
        }

        gv.addln(gv.end_graph());
        gv.increaseDpi();
    }


    private void criarArquivo() {
        File arquivoSaida = new File("C:\\Users\\pedro\\Documents\\CompiladorJava-master\\src\\afd\\oi." + extensaoArquivo);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), extensaoArquivo, tipoDeRepresentacao), arquivoSaida);
    }
}
