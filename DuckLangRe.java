public class DuckLangRe {
    public static void main(String[] args) {
        String regex = "ab|ab"; // Example regex
        ThompsonConstruction tc = new ThompsonConstruction();
        NFA nfa = tc.reToNFA(regex);
        System.out.println("NFA construction completed.");
        nfa.printNFA();
    }
}