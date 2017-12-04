import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
/**
 * RuleBaseSystem
 * 
 */

public class RuleBaseSystem {
	static RuleBase rb;
	
	public static void main(String arg[]) {
		if(arg.length==4){
		String filename=arg[0];
		String data=arg[1];
		String ques=arg[2];
		String memo=arg[3];
		ArrayList<String> s=question(filename,data,ques,memo);
		for(String l:s){
			System.out.println(l);
		}

makegraph();
		}
		else{
			
		}
	}
	public static void makegraph(){

		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());
		ArrayList<String> l = RuleBase.graph;
		for(String l1 :l){
			gv.addln(l1);
		}
		ArrayList<String> list = RuleBase.graph1;
		for(String l2 :list){
			gv.addln(l2);
		}

		gv.addln(gv.end_graph());
		System.out.println(gv.getDotSource());
		String type = "png";
		File out = new File("forward." + type); // out.gif in this example
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
	}
public static ArrayList<String> question(String fileName,String rule,String que,String memo){
	rb = new RuleBase(rule);
	ArrayList<String>  ans=new ArrayList<String>();
		String pattern[] = new String[2];
		Pattern pat = Pattern.compile("What is this?");
		java.util.regex.Matcher mat = pat.matcher(que);

		if(mat.find()){
		 pattern[1] ="It is a ?x";
		}
		else{
			ArrayList<String> list= new ArrayList<String>();
			list.add("differnt question type");
			return list;
		}

		//pattern[1]="?x is a Honda";
		try { // ファイル読み込みに失敗した時の例外処理のためのtry-catch構文

			// 文字コードを指定してBufferedReaderオブジェクトを作る
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName), "UTF-8"));
			// 変数lineに1行ずつ読み込むfor文
			for (String line = in.readLine(); line != null; line = in
					.readLine()) {
				rb.wm.addAssertion(line);// アサーションの追加
			}

		} catch (IOException e) {
			e.printStackTrace(); // 例外が発生した所までのスタックトレースを表示
		}
	
	// rb.wm.addAssertion(hoge);

	rb.forwardChain();
	try {		
		File file = new File(memo);
		file.createNewFile();
		if (checkBeforeWritefile(file)) {
			PrintWriter pw = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			for (String line : rb.wm.assertions) {
				pw.println(line);// 前向き推論で得られたアサーションをファイルに書き込む
			}
			pw.close();
			Unify.match(memo, pattern, 1,new HashMap<String, String>(), pattern.length);
			// System.out.println(Unify.ansList.size());
			
			if (Unify.ansList.size() != 0) {

					String[] one = Unify.ansList.toString().split("");
				  	boolean front =false;
				  	String ans1="";
				  	ArrayList<String> answer=new ArrayList<String>();
				  	for(String l:one){

				  		if(l.equals(")")){
				  			answer.add(ans1);
				  			front =false;
				  			ans1="";
				  		}
				  		if(front){
				  			ans1+=l;
				  		}
				  		if(l.equals("(")){
				  		front =true;
				  		}

				  	}
				  	if(answer.size()==0){
				  		
				  	}
				  	else{
				  		for(String list:answer){
				  			if( Character.isUpperCase( list.charAt( 0 ) ) ) {//大文字なら
				  		  		ans.add("It is a "+list);
				  			}

				  		}
				  	}

				
			} else {
				ans.add("みつかりませんでした");
			}
		} else {
			ans.add("ファイルに書き込めません");
		}
	} catch (IOException e) {
		System.out.println(e);
	}
	
	return ans;
}
	private static boolean checkBeforeWritefile(File file) {
		if (file.exists()) {
			if (file.isFile() && file.canWrite()) {
				return true;
			}
		}

		return false;
	}
}

/**
 * ワーキングメモリを表すクラス．
 * 
 * 
 */
class WorkingMemory {
	ArrayList<String> assertions;
int count=0;
	WorkingMemory() {
		assertions = new ArrayList<String>();
	}
static ArrayList<String >ass = new ArrayList<String>();
static ArrayList<String >nodename = new ArrayList<String>();
	/**
	 * マッチするアサーションに対するバインディング情報を返す （再帰的）
	 * 
	 * @param 前件を示す
	 *            ArrayList
	 * @return バインディング情報が入っている ArrayList
	 */
	public ArrayList matchingAssertions(ArrayList<String> theAntecedents) {
		ArrayList bindings = new ArrayList();
		return matchable(theAntecedents, 0, bindings);
	}

	private ArrayList matchable(ArrayList<String> theAntecedents, int n,
			ArrayList bindings) {
		String b="b";

		if (n == theAntecedents.size()) {
			return bindings;
		} else if (n == 0) {
			boolean success = false;
			for (int i = 0; i < assertions.size(); i++) {
				HashMap<String, String> binding = new HashMap<String, String>();
				if ((new Matcher()).matching((String) theAntecedents.get(n),
						(String) assertions.get(i), binding)) {
				//	System.out.println("アサーション"+assertions.get(i));
					String label="[label =\""+assertions.get(i)+"\"]";
					boolean check =false;
					for(String l:RuleBase.name){
						if(label.equals(l)){
							check=true;
						}
					}
					if(check){
						System.out.println("rabel"+RuleBase.hash.get(label));
						ass.add("\""+RuleBase.hash.get(label)+"\""+label);
						nodename.add(RuleBase.hash.get(label));
					}
					else{
					ass.add("\"b"+Integer.toString(count)+"\""+"[label =\""+assertions.get(i)+"\"]");
					nodename.add("b"+Integer.toString(count));
					}
					count++;
					bindings.add(binding);
					success = true;
				}
			}
			if (success) {
				return matchable(theAntecedents, n + 1, bindings);
			} else {
				ass.clear();
				nodename.clear();
				return null;
			}
		} else {
			boolean success = false;
			ArrayList newBindings = new ArrayList();
			for (int i = 0; i < bindings.size(); i++) {
				for (int j = 0; j < assertions.size(); j++) {
					if ((new Matcher()).matching(
							(String) theAntecedents.get(n),
							(String) assertions.get(j),
							(HashMap) bindings.get(i))) {
						newBindings.add(bindings.get(i));
						//System.out.println("アサーション"+assertions.get(j));
						String label="[label =\""+assertions.get(j)+"\"]";
						boolean check =false;
						for(String l:RuleBase.name){
							if(label.equals(l)){
								check=true;
							}
						}
						if(check){
							System.out.println("rabel"+RuleBase.hash.get(label));
							ass.add("\""+RuleBase.hash.get(label)+"\""+label);
							nodename.add(RuleBase.hash.get(label));
						}
						else{
						ass.add("\"b"+Integer.toString(count)+"\""+"[label =\""+assertions.get(j)+"\"]");
						nodename.add("b"+Integer.toString(count));
						}
						count++;
						success = true;
					}
				}
			}
			if (success) {
				return matchable(theAntecedents, n + 1, newBindings);
			} else {
				ass.clear();
				nodename.clear();
				return null;
			}
		}
	}

	/**
	 * アサーションをワーキングメモリに加える．
	 * 
	 * @param アサーションを表す
	 *            String
	 */
	public void addAssertion(String theAssertion) {
		System.out.println("ADD:" + theAssertion);
		assertions.add(theAssertion);
	}

	/**
	 * 指定されたアサーションがすでに含まれているかどうかを調べる．
	 * 
	 * @param アサーションを表す
	 *            String
	 * @return 含まれていれば true，含まれていなければ false
	 */
	public boolean contains(String theAssertion) {
		return assertions.contains(theAssertion);
	}

	/**
	 * ワーキングメモリの情報をストリングとして返す．
	 * 
	 * @return ワーキングメモリの情報を表す String
	 */
	public String toString() {
		return assertions.toString();
	}

}

/**
 * ルールベースを表すクラス．
 * 
 * 
 */
class RuleBase {
	String fileName;
	FileReader f;
	StreamTokenizer st;
	WorkingMemory wm;
	ArrayList<Rule> rules;
static ArrayList<String> graph=new ArrayList<String>();
static ArrayList<String> graph1=new ArrayList<String>();
static ArrayList<String> name=new ArrayList<String>();
static HashMap<String ,String> hash=new HashMap<String ,String>();
	RuleBase() {
		fileName = "insect.data";
		wm = new WorkingMemory();
		rules = new ArrayList<Rule>();
		loadRules(fileName);
	}
//追加
	RuleBase(String name) {
		fileName = name;
		wm = new WorkingMemory();
		rules = new ArrayList<Rule>();
		loadRules(fileName);
	}
	/**
	 * 前向き推論を行うためのメソッド
	 * 
	 */
	public void forwardChain() {
		boolean newAssertionCreated;
		// 新しいアサーションが生成されなくなるまで続ける．
		do {
			String a="a";
			int count =1;
			newAssertionCreated = false;
			for (int i = 0; i < rules.size(); i++) {
				Rule aRule = (Rule) rules.get(i);
				System.out.println("apply rule:" + aRule.getName());
				ArrayList<String> antecedents = aRule.getAntecedents();
				String consequent = aRule.getConsequent();
				// HashMap bindings = wm.matchingAssertions(antecedents);
				ArrayList bindings = wm.matchingAssertions(antecedents);
				if (bindings != null) {
					for (int j = 0; j < bindings.size(); j++) {
						// 後件をインスタンシエーション
						String newAssertion = instantiate((String) consequent,
								(HashMap) bindings.get(j));
						// ワーキングメモリーになければ成功
						if (!wm.contains(newAssertion)) {
							String n="\""+a+Integer.toString(count)+"\"[shape=record,label=\"{"+aRule.getName()+"|if";
							for(String l:antecedents){
								n+=l+"|";
							}
							n+="then"+consequent;
							
							n+="}\"]";
							System.out.println(n);
							count++;
							for(String l:WorkingMemory.nodename){
								graph1.add(l+"->"+"a"+Integer.toString(count-1));
							}
							for(String l:WorkingMemory.ass){
								graph.add(l+"->"+n);
								System.out.println("ass="+l);
							}
							name.add("[label =\""+newAssertion+"\"]");
							hash.put("[label =\""+newAssertion+"\"]","c"+Integer.toString(count));
							String n2="\"c"+Integer.toString(count)+"\""+"[label =\""+newAssertion+"\"]";
							graph.add(n+"->"+n2);
							graph1.add("a"+Integer.toString(count-1)+"->"+"c"+Integer.toString(count));
							WorkingMemory.ass.clear();
							WorkingMemory.nodename.clear();
							System.out.println("Success: " + newAssertion);
							wm.addAssertion(newAssertion);
							newAssertionCreated = true;
						}
					}
				}
			}
			System.out.println("Working Memory" + wm);
		} while (newAssertionCreated);
		System.out.println("No rule produces a new assertion");
	}

	private String instantiate(String thePattern, HashMap theBindings) {
		String result = new String();
		StringTokenizer st = new StringTokenizer(thePattern);
		for (int i = 0; i < st.countTokens();) {
			String tmp = st.nextToken();
			if (var(tmp)) {
				result = result + " " + (String) theBindings.get(tmp);
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

	private void loadRules(String theFileName) {
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
						// if(st.nextToken() == '"'){
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
						// }
					}
					// ルールの生成
					rules.add(new Rule(name, antecedents, consequent));
					break;
				default:
					System.out.println(token);
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		for (int i = 0; i < rules.size(); i++) {
			System.out.println(((Rule) rules.get(i)).toString());
		}
	}
}

/**
 * ルールを表すクラス．
 * 
 * 
 */
class Rule {
	String name;
	ArrayList<String> antecedents;
	String consequent;

	Rule(String theName, ArrayList<String> theAntecedents, String theConsequent) {
		this.name = theName;
		this.antecedents = theAntecedents;
		this.consequent = theConsequent;
	}

	/**
	 * ルールの名前を返す．
	 * 
	 * @return 名前を表す String
	 */
	public String getName() {
		return name;
	}

	/**
	 * ルールをString形式で返す
	 * 
	 * @return ルールを整形したString
	 */
	public String toString() {
		return name + " " + antecedents.toString() + "->" + consequent;
	}

	/**
	 * ルールの前件を返す．
	 * 
	 * @return 前件を表す ArrayList
	 */
	public ArrayList<String> getAntecedents() {
		return antecedents;
	}

	/**
	 * ルールの後件を返す．
	 * 
	 * @return 後件を表す String
	 */
	public String getConsequent() {
		return consequent;
	}

}

class Matcher {
	StringTokenizer st1;
	StringTokenizer st2;
	HashMap<String, String> vars;

	Matcher() {
		vars = new HashMap<String, String>();
	}

	public boolean matching(String string1, String string2,
			HashMap<String, String> bindings) {
		this.vars = bindings;
		return matching(string1, string2);
	}

	public boolean matching(String string1, String string2) {
		// System.out.println(string1);
		// System.out.println(string2);

		// 同じなら成功
		if (string1.equals(string2))
			return true;

		// 各々トークンに分ける
		st1 = new StringTokenizer(string1);
		st2 = new StringTokenizer(string2);

		// 数が異なったら失敗
		if (st1.countTokens() != st2.countTokens())
			return false;

		// 定数同士
		for (int i = 0; i < st1.countTokens();) {
			if (!tokenMatching(st1.nextToken(), st2.nextToken())) {
				// トークンが一つでもマッチングに失敗したら失敗
				return false;
			}
		}

		// 最後まで O.K. なら成功
		return true;
	}

	boolean tokenMatching(String token1, String token2) {
		// System.out.println(token1+"<->"+token2);
		if (token1.equals(token2))
			return true;
		if (var(token1) && !var(token2))
			return varMatching(token1, token2);
		if (!var(token1) && var(token2))
			return varMatching(token2, token1);
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
			vars.put(vartoken, token);
		}
		return true;
	}

	boolean var(String str1) {
		// 先頭が ? なら変数
		return str1.startsWith("?");
	}

}
