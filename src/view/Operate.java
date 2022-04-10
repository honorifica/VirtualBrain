package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import common.Cordinate;
import common.ImageGrid;
import common.Parameters;
import common.SigRecord;
import db.RecordDaoImp;

public class Operate extends JFrame implements ActionListener {

	private int originX;
	private int originY;
	private JTextField phrase1;
	private final JLabel background;
	private BrainPanel brainPanel;
	private JTextField phrase2;
	private JButton resultOutput;
	private Vector<SigRecord> recordVec;//用于存放从数据库取出来的全部数据。经由brainPanel在初始化的时候进行赋值
	private int gridSize=45;//用于道德层级统计时和处理程序统一单个区域像素大小的
	JRadioButton girdEnable;
	/**
	 * Create the frame.
	 */
	public Operate() {
		recordVec = new Vector<>();
		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		getContentPane().setLayout(null);

		resultOutput = new JButton("");
		resultOutput.setIcon(new ImageIcon(Operate.class.getResource("/image/resultOutputButton.png")));
		resultOutput.setBounds(38, 288, 225, 35);
		resultOutput.addActionListener(this);

		JButton input = new JButton("");
		input.setIcon(new ImageIcon(Operate.class.getResource("/image/inputButton.png")));
		input.setOpaque(true);
		input.setContentAreaFilled(false);
		input.setFocusPainted(false);
		input.setBorder(null);
		input.addActionListener(this);
		
		brainPanel = new BrainPanel();
		brainPanel.setOpaque(true);
		brainPanel.setBounds(325, 40, 450, 450);
		brainPanel.setLayout(null);
		getContentPane().add(brainPanel);
		recordVec = brainPanel.getRecordVec();

		JLabel brainPicture = new JLabel("");
		brainPicture.setIcon(new ImageIcon(Operate.class.getResource("/image/brain.png")));
		brainPicture.setBounds(0, 0, 450, 450);
		brainPanel.add(brainPicture);
		
		girdEnable = new JRadioButton("\u663E\u793A\u7F51\u683C");
		girdEnable.setOpaque(true);
		girdEnable.setContentAreaFilled(false);
		girdEnable.setFont(new Font("黑体", Font.PLAIN, 18));
		girdEnable.setBounds(163, 88, 100, 23);
		getContentPane().add(girdEnable);

		JLabel testPhrase = new JLabel("\u6D4B\u8BD5\u8BCD");
		testPhrase.setFont(new Font("黑体", Font.PLAIN, 20));
		testPhrase.setBounds(38, 174, 60, 30);
		getContentPane().add(testPhrase);

		JLabel benchmark = new JLabel("\u57FA\u51C6\u8BCD");
		benchmark.setFont(new Font("黑体", Font.PLAIN, 20));
		benchmark.setBounds(38, 126, 60, 30);
		getContentPane().add(benchmark);
		input.setActionCommand("input");
		input.setBounds(38, 343, 225, 35);
		getContentPane().add(input);
		resultOutput.setActionCommand("resultOutput");
		resultOutput.setOpaque(true);
		resultOutput.setContentAreaFilled(false);
		resultOutput.setFocusPainted(false);
		resultOutput.setBorder(null);
		getContentPane().add(resultOutput);

		JButton moralCompare = new JButton("");
		moralCompare.setIcon(new ImageIcon(Operate.class.getResource("/image/moralJudgeButton.png")));
		moralCompare.setBounds(38, 399, 225, 35);
		moralCompare.setOpaque(true);
		moralCompare.setContentAreaFilled(false);
		moralCompare.setFocusPainted(false);
		moralCompare.setBorder(null);
		moralCompare.addActionListener(this);
		moralCompare.setActionCommand("moralCompare");
		getContentPane().add(moralCompare);


		phrase2 = new JTextField();
		phrase2.setColumns(10);
		phrase2.setBounds(108, 174, 155, 30);
		getContentPane().add(phrase2);

		JPanel border = new JPanel();
		border.setBounds(0, 0, 800, 30);
		getContentPane().add(border);
		border.setLayout(null);
		border.setOpaque(false);//将上边框设为透明

		JButton close = new JButton("");
		close.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				close.setIcon(new ImageIcon(Operate.class.getResource("/image/close_entered.png")));
			}
			public void mouseExited(MouseEvent e) {
				close.setIcon(new ImageIcon(Operate.class.getResource("/image/close.png")));
			}
			public void mousePressed(MouseEvent e) {
				close.setIcon(new ImageIcon(Operate.class.getResource("/image/close_pressed.png")));
			}
		});
		close.setIcon(new ImageIcon(Operate.class.getResource("/image/close.png")));
		close.setOpaque(true);
		close.setContentAreaFilled(false);
		close.setFocusPainted(false);
		close.setBorder(null);
		close.addActionListener(this);
		close.setActionCommand("close");
		close.setBounds(765, 5, 20, 20);
		border.add(close);

		JButton minimize = new JButton("");
		minimize.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				minimize.setIcon(new ImageIcon(Operate.class.getResource("/image/minimize_pressed.png")));
			}
			public void mouseEntered(MouseEvent e) {
				minimize.setIcon(new ImageIcon(Operate.class.getResource("/image/minimize_entered.png")));
			}
			public void mouseExited(MouseEvent e) {
				minimize.setIcon(new ImageIcon(Operate.class.getResource("/image/minimize.png")));
			}
		});
		minimize.setIcon(new ImageIcon(Operate.class.getResource("/image/minimize.png")));
		minimize.setOpaque(true);
		minimize.setContentAreaFilled(false);
		minimize.setFocusPainted(false);
		minimize.setBorder(null);
		minimize.addActionListener(this);
		minimize.setActionCommand("minimize");
		minimize.setBounds(740, 5, 20, 20);
		border.add(minimize);

		phrase1 = new JTextField();
		phrase1.setBounds(108, 126, 155, 30);
		getContentPane().add(phrase1);
		phrase1.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("\u8BCD\u7EC4");
		lblNewLabel_1.setFont(new Font("黑体", Font.PLAIN, 40));
		lblNewLabel_1.setBounds(38, 69, 169, 47);
		getContentPane().add(lblNewLabel_1);

		JButton paint = new JButton("");
		paint.setIcon(new ImageIcon(Operate.class.getResource("/image/paintButton.png")));
		paint.setBounds(38, 232, 100, 35);
		paint.setOpaque(true);
		paint.setContentAreaFilled(false);
		paint.setFocusPainted(false);
		paint.setBorder(null);
		paint.addActionListener(this);
		paint.setActionCommand("paint");
		getContentPane().add(paint);

		JButton compare = new JButton("");
		compare.setIcon(new ImageIcon(Operate.class.getResource("/image/compareButton.png")));
		compare.setBounds(163, 232, 100, 35);
		compare.setOpaque(true);
		compare.setContentAreaFilled(false);
		compare.setFocusPainted(false);
		compare.setBorder(null);
		compare.addActionListener(this);
		compare.setActionCommand("compare");
		getContentPane().add(compare);
		
		background = new JLabel("");
		background.setIcon(new ImageIcon(Operate.class.getResource("/image/background.png")));
		background.setBounds(0, 0, 800, 500);
		getContentPane().add(background);
		JFrame thisFrame = this;

		border.addMouseListener(new MouseAdapter(){
		@Override
		public void mousePressed(MouseEvent e){
				           // 记录鼠标按下时的点
				            originX = e.getX();
				            originY = e.getY();
				      }
				});
			    border.addMouseMotionListener(new MouseMotionAdapter(){
			          @Override
			    public void mouseDragged(MouseEvent e){
			               // 拖拽时移动
			          Point point = thisFrame.getLocation();
			               // 偏移距离
			          int offsetX = e.getX() - originX;
			          int offsetY = e.getY() - originY;
			          thisFrame.setLocation(point.x + offsetX, point.y + offsetY);
			   }
		});

		this.setBackground(new Color(0,0,0,0));
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//是否绘制网格拟合图
		if(girdEnable.isSelected())	brainPanel.setPaintGrid(true);
		else brainPanel.setPaintGrid(false);
		//按键监听
		if (e.getActionCommand().equals("close")) {
			this.dispose();
		} else if (e.getActionCommand().equals("minimize")) {
			this.setExtendedState(ICONIFIED);
		} else if (e.getActionCommand().equals("paint")) {
			String phrase1 = this.phrase1.getText();
			String phrase2 = this.phrase2.getText();
			Vector<String> phraseVec = new Vector<>();
			if(phrase1.length()!=0)phraseVec.add(phrase1);
			if(phrase2.length()!=0)phraseVec.add(phrase2);
			if(phraseVec.size()!=0) {
				brainPanel.setPaintType(0);
				brainPanel.setPhraseVec(phraseVec);
				brainPanel.setMarkDots(true);//设置新的paint标志
				brainPanel.repaint();
			}
		} else if(e.getActionCommand().equals("compare")) {
			String phrase1 = this.phrase1.getText();
			String phrase2 = this.phrase2.getText();
			Vector<String> phraseVec = new Vector<>();
			if(phrase1.length()!=0)phraseVec.add(phrase1);
			if(phrase2.length()!=0)phraseVec.add(phrase2);
			if(phraseVec.size()!=0) {
				brainPanel.setPaintType(1);
				brainPanel.setPaintGrid(false);
				brainPanel.setPhraseVec(phraseVec);
				brainPanel.setMarkDots(true);//设置新的paint标志
				brainPanel.repaint();
			}
		} else if(e.getActionCommand().equals("resultOutput")) {
			BufferedImage resultCapture = null;
			try {
				resultCapture = new Robot().createScreenCapture(
						new Rectangle(brainPanel.getX() + this.getX(), brainPanel.getY() + this.getY(), brainPanel.getWidth(), brainPanel.getHeight()));
				String filePath = new File("").getAbsolutePath().replace('\\', '/') + "/result.jpg";
				ImageIO.write(resultCapture, "jpg", new File(filePath));
				JOptionPane.showMessageDialog(null, "导出成功，导出位置为\n " + filePath);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if(e.getActionCommand().equals("input")) {
			String absolutePath=new File("").getAbsolutePath();
			List<File> fileList = new ArrayList<>();//用于存文件的信息
			File fileDir = new File(new File("").getAbsolutePath()+"\\input");
			File[] files = fileDir.listFiles();// 获取目录下的所有文件或文件夹
			if(files==null) {
				fileDir.mkdir();
				JOptionPane.showMessageDialog(null, "请将彩色的脑地形图放入文件夹：\n"+new File("").getAbsolutePath()+"\\input");
			}
			else{
				for (File f : files) {
				fileList.add(f);
				}
				for (File f1 : fileList) {
					String imagePath = absolutePath + "\\input\\" + f1.getName();
					ImageGrid ig = new ImageGrid(imagePath);
					RecordDaoImp recordDao = new RecordDaoImp();
					String fnToPhrase=f1.getName();
					fnToPhrase = fnToPhrase.replaceAll(".jpg", "");
					fnToPhrase = fnToPhrase.replaceAll(".png","");
					recordDao.addRecord(ig.getNormalizedColorVec(Parameters.corAmount), fnToPhrase);
					f1.delete();
				}
				brainPanel.recordVecInitial();
				recordVec = brainPanel.getRecordVec();
			}
			JOptionPane.showMessageDialog(null, "尝试导入完成，导入源文件夹为：\n"+new File("").getAbsolutePath()+"\\input");
		} else if(e.getActionCommand().equals("moralCompare")) {
			if(phrase1.getText().length()==0 || phrase2.getText().length()==0)
				JOptionPane.showMessageDialog(null, "请将基准词和测试词完善");
			else{
				SigRecord baseRecord = brainPanel.recombine(recordVec, phrase1.getText());
				SigRecord testRecord = brainPanel.recombine(recordVec, phrase2.getText());
				int[][] baseGrid = new int[Parameters.gridMapSize][Parameters.gridMapSize];
				int[][] testGrid = new int[Parameters.gridMapSize][Parameters.gridMapSize];
				for(int i=0;i<Parameters.gridMapSize;i++) {
					for(int j=0;j<Parameters.gridMapSize;j++) {
						baseGrid[i][j]=0;
						testGrid[i][j]=0;
					}
				}
				Vector<Cordinate> baseCorVec = baseRecord.getCordinates();
				Vector<Cordinate> testCorVec = testRecord.getCordinates();
				for (Cordinate element : baseCorVec) {
					baseGrid[element.getX()/gridSize][element.getY()/gridSize]++;
				}
				for (Cordinate element : testCorVec) {
					testGrid[element.getX()/gridSize][element.getY()/gridSize]++;
				}
				int levelDiff = 0;
				for(int i=0;i<Parameters.gridMapSize;i++) {
					for(int j=0;j<Parameters.gridMapSize;j++) {
						levelDiff+=testGrid[i][j]%5-baseGrid[i][j];
					}
				}
				String information = "相较于基准词，测试词的评价等级为：";
				if(levelDiff>=-40 && levelDiff<=40) {
					information = information+"道德层级接近";
				}else if(levelDiff>=40&&levelDiff<=80) {
					information = information+"有一些不道德";
				}else if(levelDiff>=80&&levelDiff<=120) {
					information = information+"有较大不道德";
				}else if(levelDiff>=120&&levelDiff<=160) {
					information = information+"不道德";
				}else if(levelDiff>=160) {
					information = information+"极大不道德";
				}else if(levelDiff>=-80 && levelDiff<=-40) {
					information = information+"有一些容忍性";
				}else if(levelDiff>=-120 && levelDiff<=-80) {
					information = information+"有较大容忍性";
				}else if(levelDiff>=-160&levelDiff<=-120) {
					information = information+"可以容忍";
				}else if(levelDiff<-160) {
					information = information+"容忍性极强";
				}
				JOptionPane.showMessageDialog(null, information);
			}
		}
	}
}
