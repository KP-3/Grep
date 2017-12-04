import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QandAGUI extends JFrame implements ActionListener{
    ArrayList<String> rulelist = new ArrayList<String>();
    ArrayList<String> aslist = new ArrayList<String>();
    JLabel aslabel = new JLabel("Assertion-filename");
    JTextArea as = new JTextArea(30,20);
    JButton assave = new JButton("save");
    JButton asload = new JButton("load");
    JTextField asfilename = new JTextField(30);
    JTextField rulefilename = new JTextField(40);
    //saveボタン
    JButton rulesave = new JButton("save");
    //loadボタン
    JButton ruleload = new JButton("load");
    JTextArea rule = new JTextArea(20,30);
    JTextField query = new JTextField(50);
    JButton Go = new JButton("Go");

    JDialog dialog = new JDialog();

    JButton ok = new JButton("OK");
    JLabel error = new JLabel();

    JLabel Img = new JLabel();
    JTextArea answer = new JTextArea();
    JLabel after = new JLabel();
    public static void main(String args[]){
        QandAGUI frame = new QandAGUI();
        frame.setVisible(true);
    }
    QandAGUI(){


        GridBagLayout layout = new GridBagLayout();
        setSize(1000,1000);
        getContentPane().setLayout(layout);
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.weightx = 50;
        gbc1.weighty = 40;
        JPanel P2 = new JPanel();

        
        gbc1.fill = GridBagConstraints.BOTH;
        layout.setConstraints(P2,gbc1);
        add(P2);
        gbc1.gridy = 1;
        gbc1.weighty = 10;
        JPanel P3 = new JPanel();
        
        layout.setConstraints(P3,gbc1);
        add(P3);
        JPanel P4 = new JPanel();
        P4.setLayout(new BorderLayout());
        
        gbc1.gridy=2;
        gbc1.weighty=70;
        layout.setConstraints(P4,gbc1);
        add(P4);

        JPanel P5 = new JPanel();
        JScrollPane s2 = new JScrollPane(as);
        as.setLineWrap(true);

        P5.setLayout(layout);
        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 0;
        gbc4.gridy = 0;
        gbc4.weighty = 5;
        gbc4.weightx = 100;
        gbc4.insets = new Insets(5,5,5,5);
        gbc4.fill = GridBagConstraints.BOTH;
        layout.setConstraints(aslabel,gbc4);
        P5.add(aslabel);
        gbc4.gridy = 1;
        gbc4.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(asfilename,gbc4);
        P5.add(asfilename);
        gbc4.gridy = 2;
        gbc4.gridwidth = 1;
        layout.setConstraints(assave,gbc4);
        assave.addActionListener(this);
        P5.add(assave);
        gbc4.gridx = 2;
        layout.setConstraints(asload,gbc4);
        asload.addActionListener(this);
        P5.add(asload);
        gbc4.gridx = 0;
        gbc4.gridy = 3;
        gbc4.weighty = 80;
        gbc4.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(s2,gbc4);
        P5.add(s2);

        P2.setLayout(layout);
        GridBagConstraints gbc2 = new GridBagConstraints();


        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.weightx = 90;
        gbc2.gridheight = GridBagConstraints.REMAINDER;
        gbc2.fill = GridBagConstraints.BOTH;
        gbc2.insets = new Insets(5,5,5,5);
        layout.setConstraints(P5,gbc2);
        P2.add(P5);
        JLabel rulefn = new JLabel("rulelist-filename");
        gbc2.gridx = 1;
        gbc2.weightx = 22;
        gbc2.weighty = 5;
        gbc2.gridwidth = 2;
        gbc2.gridheight = 1;
        layout.setConstraints(rulefn,gbc2);
        P2.add(rulefn);

        gbc2.gridy = 1;
        layout.setConstraints(rulefilename,gbc2);
        P2.add(rulefilename);

        rulesave.addActionListener(this);
        gbc2.gridy = 2;
        gbc2.weightx = 22;
        gbc2.gridwidth = 1;
        layout.setConstraints(rulesave,gbc2);
        P2.add(rulesave);

        ruleload.addActionListener(this);
        gbc2.gridx = 2;
        layout.setConstraints(ruleload,gbc2);
        P2.add(ruleload);

        rule.setLineWrap(true);
        JScrollPane s1 = new JScrollPane(rule);

        gbc2.gridx = 1;
        gbc2.gridy = 3;
        gbc2.weightx = 90;
        gbc2.weighty = 70;
        gbc2.gridwidth = 2;
        gbc2.gridheight = GridBagConstraints.REMAINDER;

        layout.setConstraints(s1,gbc2);
        P2.add(s1);

        Go.addActionListener(this);
        P3.setLayout(layout);
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.fill = GridBagConstraints.HORIZONTAL;
        gbc3.gridx = 0;
        gbc3.gridy = 0;
        gbc3.insets = new Insets(0,3,0,3);
        JLabel question = new JLabel("Please write query");
        layout.setConstraints(question,gbc3);
        P3.add(question);
        gbc3.gridy = 1;
        gbc3.weightx = 90;
        layout.setConstraints(query,gbc3);
        P3.add(query);
        gbc3.gridx = 1;
        gbc3.weightx = 10;
        layout.setConstraints(Go,gbc3);
        P3.add(Go);
        BorderLayout bl = new BorderLayout();
        bl.setVgap(10);
        dialog.setLayout(bl);
        dialog.setSize(300,100);
        ok.setSize(100,80);

        
        
        ScrollPane scr = new ScrollPane();
        ScrollPane scr2 = new ScrollPane();
        scr.add(answer);
        scr2.add(Img);
        
        P4.add(after,BorderLayout.NORTH);
        P4.add(scr2,BorderLayout.CENTER);
        P4.add(scr, BorderLayout.SOUTH);
        
        dialog.add(error,BorderLayout.NORTH);
        dialog.add(ok,BorderLayout.CENTER);
        dialog.setVisible(false);
        ok.addActionListener(this);
    }
    public void actionPerformed(ActionEvent event){
        if(event.getSource() == rulesave){

            if(rulefilename.getText().equals("")){
                error.setText("Please write rulelist-filename");
                dialog.setVisible(true);
            }else{
                try{
                    String filename = rulefilename.getText();
                    BufferedWriter in = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"));
                    String[] s = rule.getText().split("\n");
                    for(String str: s){
                        rulelist.add(str);
                    }
                    for(String str: rulelist){
                        in.write(str);
                        in.newLine();
                    }
                    in.close();
                }catch(IOException e){
                    error.setText("Error!!");
                    dialog.setVisible(true);
                }
            }
        }else if(event.getSource() == ruleload){
            if(rulefilename.getText().equals("")){
                error.setText("Please write rulelist-filename");
                dialog.setVisible(true);
            }else{
                try{
                    String filename = rulefilename.getText();
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
                    for (String line = in.readLine();line != null;line = in.readLine()){
                        rulelist.add(line);
                    }
                    for(String s: rulelist){
                        rule.append(s+"\n");
                    }
                }catch (IOException e){
                    error.setText("File is not exist");
                    dialog.setVisible(true);
                }
            }
        }else if(event.getSource() == Go){
                    String rule = rulefilename.getText();
                    String assertion = asfilename.getText();
                    String que = query.getText();
                    String filename = assertion + "-after.txt";
                    if(que.equals("What is this?")){
                    ArrayList<String> ans = RuleBaseSystem.question(assertion,rule,que,filename);
                    RuleBaseSystem.makegraph();
                    ImageIcon icon = new ImageIcon("./forward.png");
                    Image small = icon.getImage().getScaledInstance((int)(icon.getIconWidth()*0.5), (int)(icon.getIconHeight()*0.5), Image.SCALE_SMOOTH);
                    ImageIcon smallIcon = new ImageIcon(small);
                    Img.setIcon(smallIcon);
                    for(String s: ans){
                    	answer.append(s+"\n");
                    }
                    after.setText(filename+"に推論後のワーキングメモリを保存しました。");
                    }else{
                    	Pattern pat1 = Pattern.compile("What is [a-z]*");
                        Matcher mat1 = pat1.matcher(que);
                        Pattern pat2 = Pattern.compile("Does [a-zA-Z]* have [a-z]*");
                        Matcher mat2 = pat2.matcher(que);
                        Pattern pat3 = Pattern.compile("Is [a-zA-Z]* [a-z]*");
                        Matcher mat3 = pat3.matcher(que);
                        Pattern pat4 = Pattern.compile("How many [a-z]* does [a-zA-Z]* have");
                        Matcher mat4 = pat4.matcher(que);
                        String hypothesis = null;
                        String subject = null;
                        String object = null;
                        int mode = 0;
                        if (mat1.find()) { // What is ~ ? ifのアサーションを全て出力
                            subject = que.substring(8, que.length() - 2);
                            hypothesis = "?x is " + subject;
                            mode = 1;
                        } else if (mat2.find()) { // Does ~ have ~ ?
                            subject = que.substring(5, que.indexOf("have") - 1);
                            hypothesis = "?x is a " + subject;
                            object = que.substring(que.indexOf("have") + 5);
                            que = " has " + object;
                            mode = 2;
                        } else if (mat3.find()) { // Is ~ ~ ?
                            subject = que.substring(3).substring(0, que.substring(3).indexOf(" "));
                            hypothesis = "?x is a " + subject;
                            object = que.substring(3).substring(que.substring(3).indexOf(" "));
                            que = " is a" + object;
                            mode = 3;
                        } else if (mat4.find()) { // How many ~ does ~ have ?
                            subject = que.substring(que.indexOf("does") + 5, que.indexOf("have") - 1);
                            hypothesis = "?x is a " + subject;
                            object = que.substring(9, que.indexOf("does") - 1);
                            que = " has " + object;
                            mode = 4;
                        }
                        ArrayList<String> ans = BackwardRuleBaseSystem.backward(assertion, rule, hypothesis, que, mode);
                        BackwardRuleBaseSystem.makegraph();
                        ImageIcon icon = new ImageIcon("./back1.png");
                        Image small = icon.getImage().getScaledInstance((int)(icon.getIconWidth()*0.7), (int)(icon.getIconHeight()*0.5), Image.SCALE_SMOOTH);
                        ImageIcon smallIcon = new ImageIcon(small);
                        Img.setIcon(smallIcon);
                        for(String s: ans){
                        	answer.append(s+"\n");
                        }
                    }
        }else if(event.getSource() == ok){
            dialog.setVisible(false);
        }else if(event.getSource() == assave){
            if(rulefilename.getText().equals("")){
                error.setText("Please write assertionlist-filename");
                dialog.setVisible(true);
            }else{
                try{
                    String filename = asfilename.getText();
                    BufferedWriter in = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-8"));
                    String[] s1 = as.getText().split("\n");
                    for(String str: s1){
                        aslist.add(str);
                    }
                    for(String str: aslist){
                        in.write(str);
                        in.newLine();
                    }
                    in.close();
                }catch(IOException e){
                    error.setText("Error!!");
                    dialog.setVisible(true);
                }
            }
        }else if(event.getSource() == asload){
            if(asfilename.getText().equals("")){
                error.setText("Please write assertionlist-filename");
                dialog.setVisible(true);
            }else{
                try{
                    String filename = asfilename.getText();
                    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
                    for (String line = in.readLine();line != null;line = in.readLine()){
                        aslist.add(line);
                    }
                    for(String str: aslist){
                        System.out.println(str);
                        as.append(str+"\n");
                    }
                }catch (IOException e){
                    error.setText("File is not exist");
                    dialog.setVisible(true);
                }
            }
        }
    }
}
