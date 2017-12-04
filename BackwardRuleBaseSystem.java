import java.lang.reflect.Array;
import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackwardRuleBaseSystem {
    static BRuleBase rb;
    static FileManager fm;

    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: %java RuleBaseSystem [query strings]");
            System.out.println("Example:");
            System.out.println(" \"?x is b\" and \"?x is c\" are queries");
            System.out.println("  %java RuleBaseSystem \"?x is b,?x is c\"");
        } else {
//            fm = new FileManager();
//            ArrayList<Rule> rules = fm.loadRules("CarShop.data");
//            ArrayList rules = fm.loadRules("AnimalWorld.data");
//            ArrayList<String> wm = fm.loadWm("CarShopWm.data");
//            ArrayList wm = fm.loadWm("AnimalWorldWm.data");
//            ArrayList<Rule> rules = fm.loadRules("Insect.data");
//            ArrayList<String> wm = fm.loadWm("InsectWm.data");
//            rb = new RuleBase(rules, wm);
//            StringTokenizer st = new StringTokenizer(args[0], ",");
//            ArrayList<String> queries = new ArrayList<String>();
//            for (int i = 0; i < st.countTokens(); ) {
//                queries.add(st.nextToken());
//            }
//            rb.backwardChain(queries);
            // ファイル名指定
            String wmFileName = "InsectWm.data";
            String rbFileName = "Insect.data";

            String question = args[0]; // 第1引数に検証したい仮説
            // 正規表現のパターンを作成
            Pattern pat1 = Pattern.compile("What is [a-z]*");
            Matcher mat1 = pat1.matcher(question);
            Pattern pat2 = Pattern.compile("Does [a-zA-Z]* have [a-z]*");
            Matcher mat2 = pat2.matcher(question);
            Pattern pat3 = Pattern.compile("Is [a-zA-Z]* [a-z]*");
            Matcher mat3 = pat3.matcher(question);
            Pattern pat4 = Pattern.compile("How many [a-z]* does [a-zA-Z]* have");
            Matcher mat4 = pat4.matcher(question);
//            Pattern pat4 = Pattern.compile("Is it [a-z]*");
//            Matcher mat4 = pat4.matcher(args[0]);
            String hypothesis = null;
            String que = null;
            String subject = null; // 主語
            String object = null; // 目的語
            int mode = 0;
            if (mat1.find()) { // What is ~ ? ifのアサーションを全て出力
                subject = question.substring(8, question.length() - 2);
                hypothesis = "?x is " + subject;
                mode = 1;
            } else if (mat2.find()) { // Does ~ have ~ ?
                subject = question.substring(5, question.indexOf("have") - 1);
                hypothesis = "?x is a " + subject;
                object = question.substring(question.indexOf("have") + 5);
                que = " has " + object;
                mode = 2;
            } else if (mat3.find()) { // Is ~ ~ ?
                subject = question.substring(3).substring(0, question.substring(3).indexOf(" "));
                hypothesis = "?x is a " + subject;
                object = question.substring(3).substring(question.substring(3).indexOf(" "));
                que = " is a" + object;
                mode = 3;
            } else if (mat4.find()) { // How many ~ does ~ have ?
                subject = question.substring(question.indexOf("does") + 5, question.indexOf("have") - 1);
                hypothesis = "?x is a " + subject;
                object = question.substring(9, question.indexOf("does") - 1);
                que = " has " + object;
                mode = 4;
            }
//            hypothesis = "?x is a Carolla Wagon";
//            que = "his-car has several seats";
            System.out.println("Question:" + question);
//            System.out.println(hypothesis);
//            System.out.println(que);
//            System.out.println(Integer.toString(mode));
            ArrayList<String> ans = backward(wmFileName, rbFileName, hypothesis, que, mode);
	    System.out.println(ans);
        }
        makegraph();
    }

    public static ArrayList<String> backward(String wmFileName, String rbFileName, String hypothesis, String que, int mode) {
        fm = new FileManager();
        ArrayList<BRule> rules = fm.loadRules(rbFileName);
        //ArrayList rules = fm.loadRules("AnimalWorld.data");
        ArrayList<String> wm = fm.loadWm(wmFileName);
        //ArrayList wm    = fm.loadWm("AnimalWorldWm.data");
        rb = new BRuleBase(rules, wm);
//        System.out.println(rb.wm.toString()); // 数字に対してエラーをはく
        StringTokenizer st = new StringTokenizer(hypothesis, ",");
        ArrayList<String> queries = new ArrayList<String>();
        for (int i = 0; i < st.countTokens(); ) {
            queries.add(st.nextToken());
        }
	ArrayList<String> theFired = new ArrayList<String>();
        try {
        	theFired = rb.backwardChain(queries); // 後向き推論の実行
        } catch (Exception e){
        	System.out.println(e);
        }
        boolean success = true;
        if (mode != 1) {
        	try {
        		que = theFired.get(0).substring(0, theFired.get(0).indexOf(" ")) + que;
        	} catch (Exception e) {
        		System.out.println(e);
        		success = false;
        	}
        }
        System.out.println();
        System.out.println("Answer");
        ArrayList<String> ansList = new ArrayList<String>();
	if (success) {
        	if (mode == 1) {
        		String subject = hypothesis.substring(8); // 主語を取得
//            	System.out.println(subject);
		boolean result = false;
        		for (String s : theFired) {
        			if (!s.substring(s.indexOf(" is ") + 1).equals(hypothesis.substring(hypothesis.indexOf(" is ") + 1))) { // theFiredの末尾にはhypothesisが入っている
        				if (s.contains(" is ")) { // ~ is ~ という形式のアサーションのみ出力
					result = true;
        					String ans = subject + s.substring(s.indexOf(" is "));
        					System.out.println(ans);
        					ansList.add(ans);
        				}
        			}	
        		}
			if (!result) {
					String ans = "I don't know.";
					System.out.println(ans);
					ansList.add(ans);
				}
//            if (theFired.contains(que)) {
//                System.out.println("Exists");
//            } else {
//                System.out.println("Not Exists");
//            }
        } else if (mode == 2) {
            Pattern patSubject = Pattern.compile(que.substring(0, que.indexOf("has") + 3));
//            System.out.println(patSubject);
            Pattern patObject = Pattern.compile(que.substring(que.indexOf("has") + 4));
//            System.out.println(patObject);
		boolean result = false;
            for (String s : theFired) {
                Matcher matSubject = patSubject.matcher(s);
                if (matSubject.find()) {
//                    System.out.println("success");
                    Matcher matObject = patObject.matcher(s);
                    if (matObject.find()) {
//                        System.out.println("Exists");
		result = true;
                    	String ans = "Yes, it has.";
                    	System.out.println(ans);
                        ansList.add(ans);
                    }
                }
            }
	    if (!result) {
					String ans = "No, it hasn't.";
					System.out.println(ans);
					ansList.add(ans);
				}
        } else if (mode == 3) {
            Pattern patSubject = Pattern.compile(que.substring(0, que.indexOf("is") + 3));
//            System.out.println(patSubject);
            Pattern patObject = Pattern.compile(que.substring(que.indexOf("is") + 4));
//            System.out.println(patObject);
		boolean result = false;
            for (String s : theFired) {
                Matcher matSubject = patSubject.matcher(s);
                if (matSubject.find()) {
//                    System.out.println("success");
                    Matcher matObject = patObject.matcher(s);
                    if (matObject.find()) {
//                        System.out.println("Exists");
			result = true;
                    	String ans = "Yes, it is.";
                        System.out.println(ans);
                        ansList.add(ans);
                    }
                }
            }
	    if (!result) {
					String ans = "No, it isn't.";
					System.out.println(ans);
					ansList.add(ans);
				}
        } else if (mode == 4) {
            Pattern patSubject = Pattern.compile(que.substring(0, que.indexOf("has") + 3));
//            System.out.println(patSubject);
            Pattern patObject = Pattern.compile(que.substring(que.indexOf("has") + 4));
//            System.out.println(patObject);
		boolean result = false;
            for (String s : theFired) {
                Matcher matSubject = patSubject.matcher(s);
                if (matSubject.find()) {
//                    System.out.println("success");
                    Matcher matObject = patObject.matcher(s);
                    if (matObject.find()) {
//                        System.out.println("Exists");
			result = true;
                        s = s.replaceAll(patSubject.toString(), "");
                        s = s.replaceAll("-" + patObject.toString(), "");
                        s = s.replaceAll(" ", "");
                        String ans = patSubject + " " + s + " " + patObject + ".";
                        System.out.println(ans);
                        ansList.add(ans);
                    }
                }
            }
	    if (!result) {
					String ans = "I don't know.";
					System.out.println(ans);
					ansList.add(ans);
				}
        }
        } else {
        	if (mode == 1) {
        		String ans = "I don't know.";
        		System.out.println(ans);
        		ansList.add(ans);
        	} else if (mode == 2) {
        		String ans = "No, it hasn't.";
        		System.out.println(ans);
        		ansList.add(ans);
        	} else if (mode == 3) {
        		String ans = "No, it isn't.";
        		System.out.println(ans);
        		ansList.add(ans);
        	} else if (mode == 4) {
        		String ans = "I don't know.";
        		System.out.println(ans);
        		ansList.add(ans);
        	}
        }
	return ansList;
    }

    public static void makegraph() {

        GraphViz gv = new GraphViz();
        gv.addln(gv.start_graph());
        ArrayList<String> l = BRuleBase.graph;
        for (String l1 : l) {
            gv.addln(l1);
        }
        ArrayList<String> list = BRuleBase.graph1;
        for (String l2 : list) {
            gv.addln(l2);
        }

        gv.addln(gv.end_graph());
        //System.out.println(gv.getDotSource());
        String type = "png";
        File out = new File("back1." + type); // out.gif in this example
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
    }

//        System.out.println(rb.theFired.toString());
}

class BRuleBase implements Serializable {
    String fileName;
    ArrayList<String> wm;
    ArrayList<BRule> rules;
    //追加
    static ArrayList<String> graph = new ArrayList<String>();
    static ArrayList<String> graph1 = new ArrayList<String>();
    static ArrayList<String> oldgraph = new ArrayList<String>();
    static ArrayList<String> oldgraph1 = new ArrayList<String>();
    static ArrayList<String> oldgraph2 = new ArrayList<String>();
    static ArrayList<String> oldgraph3 = new ArrayList<String>();
    static ArrayList<String> name = new ArrayList<String>();
    static HashMap<String, String> hash = new HashMap<String, String>();
    int count = 0;

    BRuleBase(ArrayList<BRule> theRules, ArrayList<String> theWm) {
        wm = theWm;
        rules = theRules;
//        ansFired = new ArrayList<String>();
    }

    public void setWm(ArrayList<String> theWm) {
        wm = theWm;
    }

    public void setRules(ArrayList<BRule> theRules) {
        rules = theRules;
    }

    public ArrayList<String> backwardChain(ArrayList<String> hypothesis) {
        System.out.println("Hypothesis:" + hypothesis);
        ArrayList<String> orgQueries = (ArrayList) hypothesis.clone();
        //HashMap<String,String> binding = new HashMap<String,String>();
        HashMap<String, String> binding = new HashMap<String, String>();
        ArrayList<String> theFired = new ArrayList<String>();
        if (matchingPatterns(hypothesis, binding, theFired)) {
            System.out.println("Yes");
            System.out.println(binding);
            // 最終的な結果を基のクェリーに代入して表示する
            for (int i = 0; i < orgQueries.size(); i++) {
                String aQuery = (String) orgQueries.get(i);
                System.out.println("binding: " + binding);
                String anAnswer = instantiate(aQuery, binding);
                System.out.println("Query: " + aQuery);
                System.out.println("Answer:" + anAnswer);
                theFired.add(anAnswer);
                System.out.println("theFired:" + theFired);
//                System.out.println("ansFired:" + ansFired);
            }
        } else {
            System.out.println("No");
        }
        return theFired;
    }

    /**
     * マッチするワーキングメモリのアサーションとルールの後件
     * に対するバインディング情報を返す
     */
    private boolean matchingPatterns(ArrayList<String> thePatterns, HashMap<String, String> theBinding, ArrayList<String> theFired) {
        String firstPattern;
        if (thePatterns.size() == 1) {
            firstPattern = (String) thePatterns.get(0);
            if (matchingPatternOne(firstPattern, theBinding, 0, theFired) != -1) {
                return true;
            } else {
                return false;
            }
        } else {
            firstPattern = (String) thePatterns.get(0);
            thePatterns.remove(0);

            int cPoint = 0; // choice point
            while (cPoint < wm.size() + rules.size()) {
                // 元のバインディングを取っておく
                HashMap<String, String> orgBinding = new HashMap<String, String>();
                for (Iterator<String> i = theBinding.keySet().iterator(); i.hasNext(); ) {
                    String key = i.next();
                    String value = (String) theBinding.get(key);
                    orgBinding.put(key, value);
                }
                ArrayList<String> orgFired = theFired; // 元のtheFiredを取っておく
                //元のノード関係をとっておく
                oldgraph2.clear();
                oldgraph3.clear();
                for (String l : oldgraph) {
                    oldgraph2.add(l);
                }
                for (String l : oldgraph1) {
                    oldgraph3.add(l);
                }
                int tmpPoint = matchingPatternOne(firstPattern, theBinding, cPoint, theFired);
                System.out.println("tmpPoint: " + tmpPoint);
                if (tmpPoint != -1) {
                    System.out.println("Success:" + firstPattern);
                    theFired.add(firstPattern); // 途中のアサーションをtheFiredに追加
                    System.out.println("thePatterns: " + thePatterns.toString());
                    System.out.println("theFired: " + theFired);
                    if (matchingPatterns(thePatterns, theBinding, theFired)) {
                        //成功
                        System.out.println("Success");
//                        ansFired = theFired;
                        return true;
                    } else {
                        //失敗
                        //choiceポイントを進める
                        cPoint = tmpPoint;
                        // 失敗したのでバインディングを戻す
                        theBinding.clear();
                        for (Iterator<String> i = orgBinding.keySet().iterator(); i.hasNext(); ) {
                            String key = i.next();
                            String value = orgBinding.get(key);
                            theBinding.put(key, value);
                        }
//                        theFired = orgFired; // 失敗したら元に戻す
                        theFired.clear();
                        //ノード関係の復元
                        oldgraph.clear();
                        oldgraph1.clear();
                        for (String l : oldgraph2) {
                            oldgraph.add(l);
                        }
                        for (String l : oldgraph3) {
                            oldgraph1.add(l);
                        }
                    }
                } else {
                    // 失敗したのでバインディングを戻す
                    theBinding.clear();
                    for (Iterator<String> i = orgBinding.keySet().iterator(); i.hasNext(); ) {
                        String key = i.next();
                        String value = orgBinding.get(key);
                        theBinding.put(key, value);
                    }
                    theFired.clear();
                    //ノード関係の復元
                    oldgraph.clear();
                    oldgraph1.clear();
                    for (String l : oldgraph2) {
                        oldgraph.add(l);
                    }
                    for (String l : oldgraph3) {
                        oldgraph1.add(l);
                    }
                    return false;
                }
            }
            return false;
        /*
        if(matchingPatternOne(firstPattern,theBinding)){
	      return matchingPatterns(thePatterns,theBinding);
	    } else {
	      return false;
	    }
	    */
        }
    }

    private int matchingPatternOne(String thePattern, HashMap<String, String> theBinding, int cPoint, ArrayList<String> theFired) {
        if (cPoint < wm.size()) {
            // WME(Working Memory Elements) と Unify してみる．
            for (int i = cPoint; i < wm.size(); i++) {
                if ((new BUnifier()).unify(thePattern, (String) wm.get(i), theBinding)) {
                    System.out.println("Success WM");
                    System.out.println((String) wm.get(i) + " <=> " + thePattern);
                    theFired.add(wm.get(i)); // 発火したアサーションを追加
                    System.out.println("theFired: " + theFired.toString());
                    String m = "";
                    String label = "[label =\"" + thePattern + "\"]";
                    String m1 = "";
                    boolean check = false;
                    for (String l : name) {
                        if (label.equals(l)) {
                            check = true;
                        }
                    }
                    if (check) {
                        m = "\"" + hash.get(label) + "\"" + label;
                        m1 = hash.get(label);
                    } else {
                        m = "\"b" + Integer.toString(count) + "\"" + "[label=\"" + thePattern + "\"]";
                        m1 = "b" + Integer.toString(count);
                    }
                    String n = "\"d" + Integer.toString(count) + "\"" + "[label =\"" + (String) wm.get(i) + "\"]";

                    graph.add(m + "->" + n);
                    graph1.add(m1 + "->" + "d" + Integer.toString(count));
                    count++;
                    return i + 1;
                }
            }
        }
        if (cPoint < wm.size() + rules.size()) {
            // Ruleと Unify してみる．
            for (int i = cPoint; i < rules.size(); i++) {
                BRule aRule = rename(rules.get(i));
                // 元のバインディングを取っておく．
                HashMap<String, String> orgBinding = new HashMap<String, String>();
                for (Iterator<String> itr = theBinding.keySet().iterator(); itr.hasNext(); ) {
                    String key = itr.next();
                    String value = theBinding.get(key);
                    orgBinding.put(key, value);
                }
                ArrayList<String> orgFired = theFired; // 元のtheFiredを取っておく
                if ((new BUnifier()).unify(thePattern, (String) aRule.getConsequent(), theBinding)) {
                    System.out.println("Success RULE");
                    System.out.println("Rule:" + aRule + " <=> " + thePattern);
                    ArrayList<String> antecedents = aRule.getAntecedents();
                    String consequent = aRule.getConsequent();
                    String m = "";
                    String label = "[label =\"" + thePattern + "\"]";
                    String m1 = "";
                    boolean check = false;
                    for (String l : name) {
                        if (label.equals(l)) {
                            check = true;
                        }
                    }
                    if (check) {
                        m = "\"" + hash.get(label) + "\"" + label;
                        m1 = hash.get(label);
                    } else {
                        m = "\"b" + Integer.toString(count) + "\"" + "[label=\"" + thePattern + "\"]";
                        m1 = "b" + Integer.toString(count);
                    }
                    String n = "\"a" + Integer.toString(count) + "\"[shape=record,label=\"{" + aRule.getName() + "|if";
                    String a = "a" + Integer.toString(count);
                    for (String l : antecedents) {
                        n += l + "|";
                    }
                    n += "then" + consequent;
                    n += "}\"]";
                    System.out.println(n);
                    oldgraph.clear();
                    oldgraph1.clear();
                    for (String l : graph) {
                        oldgraph.add(l);
                    }
                    for (String l : graph1) {
                        oldgraph1.add(l);
                    }
                    graph.add(m + "->" + n);
                    graph1.add(m1 + "->" + a);
                    for (String n1 : antecedents) {
                        String n2 = "\"c" + Integer.toString(count) + "\"" + "[label=\"" + n1 + "\"]";
                        name.add("[label =\"" + n1 + "\"]");
                        hash.put("[label =\"" + n1 + "\"]", "c" + Integer.toString(count));
                        graph1.add(a + "->" + "c" + Integer.toString(count));
                        count++;
                        graph.add(n + "->" + n2);
                    }
                    // さらにbackwardChaining
                    ArrayList<String> newPatterns = aRule.getAntecedents();
                    System.out.println("newPatterns: " + newPatterns.toString());
                    System.out.println("theBinding: " + theBinding);
                    if (matchingPatterns(newPatterns, theBinding, theFired)) {
                        return wm.size() + i + 1;
                    } else {
                        // 失敗したら元に戻す．
                        theBinding.clear();
                        for (Iterator<String> itr = orgBinding.keySet().iterator(); itr.hasNext(); ) {
                            String key = itr.next();
                            String value = orgBinding.get(key);
                            theBinding.put(key, value);
                        }
//                        theFired = orgFired; // 失敗したら元に戻す
                        theFired.clear();
                        graph.clear();
                        graph1.clear();
                        for (String l : oldgraph) {
                            graph.add(l);
                        }
                        for (String l : oldgraph1) {
                            graph1.add(l);
                        }
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 与えられたルールの変数をリネームしたルールのコピーを返す．
     *
     * @param 変数をリネームしたいルール
     * @return 変数がリネームされたルールのコピーを返す．
     */
    int uniqueNum = 0;

    private BRule rename(BRule theRule) {
        BRule newRule = theRule.getRenamedRule(uniqueNum);
        uniqueNum = uniqueNum + 1;
        return newRule;
    }

    private String instantiate(String thePattern, HashMap<String, String> theBindings) {
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for (int i = 0; i < st.countTokens(); ) {
            String tmp = st.nextToken();
            if (var(tmp)) {
                result = result + " " + (String) theBindings.get(tmp);
                System.out.println("tmp: " + tmp + ", result: " + result);
            } else {
                result = result + " " + tmp;
            }
        }
        return result.trim();
    }

    private boolean var(String str1) {
        // 先頭が ? なら変数
        return str1.startsWith("?");
    }
}

class FileManager {
    FileReader f;
    StreamTokenizer st;

    public ArrayList<BRule> loadRules(String theFileName) {
        ArrayList<BRule> rules = new ArrayList<BRule>();
        String line;
        try {
            int token;
            f = new FileReader(theFileName);
            st = new StreamTokenizer(f);
            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
                switch (token) {
                    case StreamTokenizer.TT_WORD:
                        String name = null;
                        ArrayList<String> antecedents = null;
                        String consequent = null;
                        if ("rule".equals(st.sval)) {
                            st.nextToken();
                            name = st.sval;
                            st.nextToken();
                            if ("if".equals(st.sval)) {
                                antecedents = new ArrayList<String>();
                                st.nextToken();
                                while (!"then".equals(st.sval)) {
                                    antecedents.add(st.sval);
                                    st.nextToken();
                                }
                                if ("then".equals(st.sval)) {
                                    st.nextToken();
                                    consequent = st.sval;
                                }
                            }
                        }
                        rules.add(
                                new BRule(name, antecedents, consequent));
                        break;
                    default:
                        System.out.println(token);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return rules;
    }

    public ArrayList<String> loadWm(String theFileName) {
        ArrayList<String> wm = new ArrayList<String>();
        String line;
        try {
            int token;
            f = new FileReader(theFileName);
            st = new StreamTokenizer(f);
            st.eolIsSignificant(true);
            st.wordChars('\'', '\'');
            while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
                line = new String();
                while (token != StreamTokenizer.TT_EOL) {
                    line = line + st.sval + " ";
                    token = st.nextToken();
                }
                wm.add(line.trim());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return wm;
    }
}


/**
 * ルールを表すクラス．
 */
class BRule implements Serializable {
    String name;
    ArrayList<String> antecedents;
    String consequent;

    BRule(String theName, ArrayList<String> theAntecedents, String theConsequent) {
        this.name = theName;
        this.antecedents = theAntecedents;
        this.consequent = theConsequent;
    }

    public BRule getRenamedRule(int uniqueNum) {
        ArrayList<String> vars = new ArrayList<String>();
        for (int i = 0; i < antecedents.size(); i++) {
            String antecedent = (String) this.antecedents.get(i);
            vars = getVars(antecedent, vars);
        }
        vars = getVars(this.consequent, vars);
        HashMap<String, String> renamedVarsTable = makeRenamedVarsTable(vars, uniqueNum);

        ArrayList<String> newAntecedents = new ArrayList<String>();
        for (int i = 0; i < antecedents.size(); i++) {
            String newAntecedent = renameVars((String) antecedents.get(i), renamedVarsTable);
            newAntecedents.add(newAntecedent);
        }
        String newConsequent = renameVars(consequent, renamedVarsTable);

        BRule newRule = new BRule(name, newAntecedents, newConsequent);
        return newRule;
    }

    private ArrayList<String> getVars(String thePattern, ArrayList<String> vars) {
        StringTokenizer st = new StringTokenizer(thePattern);
        for (int i = 0; i < st.countTokens(); ) {
            String tmp = st.nextToken();
            if (var(tmp)) {
                vars.add(tmp);
            }
        }
        return vars;
    }

    private HashMap<String, String> makeRenamedVarsTable(ArrayList<String> vars, int uniqueNum) {
        HashMap<String, String> result = new HashMap<String, String>();
        for (int i = 0; i < vars.size(); i++) {
            String newVar = (String) vars.get(i) + uniqueNum;
            result.put((String) vars.get(i), newVar);
        }
        return result;
    }

    private String renameVars(String thePattern,
                              HashMap<String, String> renamedVarsTable) {
        String result = new String();
        StringTokenizer st = new StringTokenizer(thePattern);
        for (int i = 0; i < st.countTokens(); ) {
            String tmp = st.nextToken();
            if (var(tmp)) {
                result = result + " " + renamedVarsTable.get(tmp);
            } else {
                result = result + " " + tmp;
            }
        }
        return result.trim();
    }

    private boolean var(String str) {
        // 先頭が ? なら変数
        return str.startsWith("?");
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name + " " + antecedents.toString() + "->" + consequent;
    }

    public ArrayList<String> getAntecedents() {
        return antecedents;
    }

    public String getConsequent() {
        return consequent;
    }
}

class BUnifier {
    StringTokenizer st1;
    String buffer1[];
    StringTokenizer st2;
    String buffer2[];
    HashMap<String, String> vars;

    BUnifier() {
        //vars = new HashMap();
    }

    public boolean unify(String string1, String string2, HashMap<String, String> theBindings) {
        HashMap<String, String> orgBindings = new HashMap<String, String>();
        for (Iterator<String> i = theBindings.keySet().iterator(); i.hasNext(); ) {
            String key = i.next();
            String value = theBindings.get(key);
            orgBindings.put(key, value);
        }
        this.vars = theBindings;
        if (unify(string1, string2)) {
            return true;
        } else {
            // 失敗したら元に戻す．
            theBindings.clear();
            for (Iterator<String> i = orgBindings.keySet().iterator(); i.hasNext(); ) {
                String key = i.next();
                String value = orgBindings.get(key);
                theBindings.put(key, value);
            }
            return false;
        }
    }

    public boolean unify(String string1, String string2) {
        // 同じなら成功
        if (string1.equals(string2)) return true;

        // 各々トークンに分ける
        st1 = new StringTokenizer(string1);
        st2 = new StringTokenizer(string2);

        // 数が異なったら失敗
        if (st1.countTokens() != st2.countTokens()) return false;

        // 定数同士
        int length = st1.countTokens();
        buffer1 = new String[length];
        buffer2 = new String[length];
        for (int i = 0; i < length; i++) {
            buffer1[i] = st1.nextToken();
            buffer2[i] = st2.nextToken();
        }

        // 初期値としてバインディングが与えられていたら
        if (this.vars.size() != 0) {
            for (Iterator<String> i = vars.keySet().iterator(); i.hasNext(); ) {
                String key = i.next();
                String value = vars.get(key);
                replaceBuffer(key, value);
            }
        }

        for (int i = 0; i < length; i++) {
            if (!tokenMatching(buffer1[i], buffer2[i])) {
                return false;
            }
        }

        return true;
    }

    boolean tokenMatching(String token1, String token2) {
        if (token1.equals(token2)) return true;
        if (var(token1) && !var(token2)) return varMatching(token1, token2);
        if (!var(token1) && var(token2)) return varMatching(token2, token1);
        if (var(token1) && var(token2)) return varMatching(token1, token2);
        return false;
    }

    boolean varMatching(String vartoken, String token) {
        if (vars.containsKey(vartoken)) {
            if (token.equals(vars.get(vartoken))) {
                return true;
            } else {
                return false;
            }
        } else {
            replaceBuffer(vartoken, token);
            if (vars.containsValue(vartoken)) {
                replaceBindings(vartoken, token);
            }
            vars.put(vartoken, token);
        }
        return true;
    }

    void replaceBuffer(String preString, String postString) {
        for (int i = 0; i < buffer1.length; i++) {
            if (preString.equals(buffer1[i])) {
                buffer1[i] = postString;
            }
            if (preString.equals(buffer2[i])) {
                buffer2[i] = postString;
            }
        }
    }

    void replaceBindings(String preString, String postString) {
        for (Iterator<String> i = vars.keySet().iterator(); i.hasNext(); ) {
            String key = i.next();
            if (preString.equals(vars.get(key))) {
                vars.put(key, postString);
            }
        }
    }

    boolean var(String str1) {
        // 先頭が ? なら変数
        return str1.startsWith("?");
    }
}

