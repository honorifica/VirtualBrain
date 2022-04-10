package view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;
import java.util.Vector;

import javax.swing.JPanel;

import common.Cordinate;
import common.Parameters;
import common.SigRecord;
import db.RecordDaoImp;

public class BrainPanel extends JPanel {
	private Graphics2D graphic2d;//用于进行点的绘画画布
	private SigRecord sigRecord;//内部处理完用于绘图的记录条
	private Vector<String> phraseVec;//从operate界面得到的短语组
	private Vector<SigRecord> recordVec;//数据库中读出来的全部记录
	private Vector<SigRecord> paintVec;
	private boolean markDots;//当点击绘图之后启动随机化取值,用于解决数据跳动问题
	private boolean paintGrid;//用于指示绘画时是否显示网格覆盖
	private int paintType;//用于显示选择的绘画类型以供颜色设置：0（绘画操作，只呈现），1（对比操作，作区分）
	private AlphaComposite shapeAlphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Parameters.shapeAlphaComposite);//用于调整点的透明度
	private AlphaComposite gridAlphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Parameters.gridAlphaComposite);//用于调整点的透明度
	public BrainPanel() {
		setPaintGrid(false);//默认不进行网格绘画
		phraseVec = new Vector<String>();
		recordVec = new Vector<SigRecord>();
		paintVec = new Vector<SigRecord>();
		recordVecInitial();
		setMarkDots(false);
		paintType=0;//默认只呈现
	}

	/**
	 * 用于从数据库获取全部的记录存入该panel
	 */
	public void recordVecInitial() {
		RecordDaoImp recordDao = new RecordDaoImp();
		setRecordVec(recordDao.getAllRecord());
	}
	/**
	 * 用于按照需要在上面画图
	 * 当markDots为真时进行图片的绘制标记
	 */

	@Override
	public void paint(Graphics graphic) {
		super.paint(graphic);
		graphic2d = (Graphics2D)graphic;
		graphic2d.setColor(Color.RED);//设置画图的颜色
		this.setBackground(null);
		this.setOpaque(true);
		if(markDots) {
			paintVec = new Vector<SigRecord>();
			for (String element : phraseVec) {
				sigRecord = new SigRecord();
				sigRecord = recombine(recordVec, element);
				paintVec.add(sigRecord);
			}
		}
		for(int i=0;i<paintVec.size();i++) {
			if(paintVec.get(i)!=null)
				fillTheBrain(paintVec.get(i));
			if(paintType==1) 
				graphic2d.setColor(Color.BLUE);
		}
		markDots=false;
	}
	/**
	 * 用于对取到的全部的数据进行匹配筛选
	 * @param recordVec 得到的全部数据
	 * @param phrase 需要处理的短句
	 * @return sigRecord 处理好的供fillTheBrain使用的记录
	 */
	public SigRecord recombine(Vector<SigRecord>recordVec, String phrase) {
		char[] head = new char[1];
		char[] tail = new char[1];
		int pLength = phrase.length();
		phrase.getChars(0, 1, head, 0);
		phrase.getChars(pLength-1, pLength , tail, 0);
		int ascHead = head[0];
		int ascTail = tail[0];
		Vector<String> phraseVec = new Vector<>();
		String[] phraseArr = null;
		if(isEnglish(ascHead) && isEnglish(ascTail)) {
			phraseArr = phrase.split(" ");
			int paLength = phraseArr.length;
			for(int i=0;i<paLength;i++) {
				phraseVec.add(phraseArr[i]);
			}
			if(phraseVec.size()<=3) return null;
		} else{
			phraseArr = new String[3];
			if(phrase.length()<6) {
				return null;
			} else {
				phraseArr[0] = phrase.substring(0, 2);
				phraseArr[1] = phrase.substring(phrase.length()-4,phrase.length()-2);
				phraseArr[2] = phrase.substring(phrase.length()-2, phrase.length());
			}
			int paLength = phraseArr.length;
			for(int i=0;i<paLength;i++) {
				phraseVec.add(phraseArr[i]);
			}
		}
		String pHead = phraseArr[0];
		String pTail = phraseArr[phraseArr.length-1];
		String pMid = phraseArr[phraseArr.length-2];
		Vector<SigRecord> headEqual = new Vector<>();
		Vector<SigRecord> tailEqual = new Vector<>();
		Vector<SigRecord> midEqual = new Vector<>();
		SigRecord sigRecord  = new SigRecord();
		int recordAmount = recordVec.size();
		int totalCorAmount = 0;
		for(int i=0;i<recordAmount;i++) {
			SigRecord judgingRecord = recordVec.get(i);
			int pSize = judgingRecord.getPhrase().size();
			if(judgingRecord.getPhrase().get(0).equals(pHead) && 
					judgingRecord.getPhrase().get(pSize-1).equals(pTail) && 
					judgingRecord.getPhrase().get(pSize-2).equals(pMid)
					) {
				totalCorAmount=judgingRecord.getCordinates().size();
				return judgingRecord;
			}
			if(judgingRecord.getPhrase().get(pSize-1).equals(pTail)) {
				tailEqual.add(judgingRecord);
				totalCorAmount+=judgingRecord.getCordinates().size();
			}
			if(judgingRecord.getPhrase().get(0).equals(pHead)) {
				headEqual.add(judgingRecord);
				totalCorAmount+=judgingRecord.getCordinates().size();
			}
			if(judgingRecord.getPhrase().get(pSize-2).equals(pMid)) {
				midEqual.add(judgingRecord);
				totalCorAmount+=judgingRecord.getCordinates().size();
			}
		}
		sigRecord = randmize(headEqual, tailEqual, midEqual, totalCorAmount);
		if(sigRecord==null) {
			return null;
		} else {
			sigRecord.setPhrase(phraseVec);
			return sigRecord;
		}
	}

	public boolean isEnglish(int asc) {
		if((asc >= 97 && asc<=122) ||(asc >= 65 && asc<=90)) return true;
		else return false;
	}

	/**
	 * 用于对recombine中筛选出的数据进行随机化组合
	 * @param headEqualVec 从recombine中筛出取来的动词相等的
	 * @param tailEqualVec 从recombine中筛取出来的名词相等的
	 * @param midEqualVec 	从recombine中筛出取来的形容词相等的
	 * @param totalCorAmount 所有匹配到的记录的坐标条数。用于生成当前条的坐标数
	 * @return sigRecord 返回随机重组合之后的记录供recombine返回给paint
	 */
	public SigRecord randmize(
			Vector<SigRecord> headEqualVec, Vector<SigRecord> tailEqualVec, Vector<SigRecord> midEqualVec,
			int totalCorAmount) {
		int gridAmount[][] = new int[Parameters.gridMapSize][Parameters.gridMapSize];
		for(int i = 0;i<Parameters.gridMapSize;i++) {
			for(int j=0;j<Parameters.gridMapSize;j++) {
				gridAmount[i][j]=0;
			}
		}
		Vector<Cordinate> corVec = new Vector<>();
		int rpSize = headEqualVec.size() + tailEqualVec.size() + midEqualVec.size();
		if(rpSize==0) {
			return null;
		} else {
			//根据占比进行数量选择的分配
			float rpSizeF = (float)rpSize;
			float heRatio = (float)headEqualVec.size()/rpSizeF;
			float teRatio = (float)tailEqualVec.size()/rpSizeF;
			float meRatio = (float)midEqualVec.size()/rpSizeF;
			int MAX_CODINATE_AMOUNT = totalCorAmount/rpSize;
			float heAmountFloat = MAX_CODINATE_AMOUNT*heRatio;
			float teAmountFloat = MAX_CODINATE_AMOUNT*teRatio;
			float meAmountFloat = MAX_CODINATE_AMOUNT*meRatio;
			int heAmount=(int)heAmountFloat;
			int teAmount=(int)teAmountFloat;
			int meAmount=(int)meAmountFloat;
			if(heAmount+teAmount+meAmount < MAX_CODINATE_AMOUNT) {
				if(heAmount <= teAmount && heAmount <= meAmount && heAmount != 0 )
					heAmount++;
				if(meAmount <= heAmount && meAmount <= teAmount && meAmount!=0 )
					meAmount++;
				if(teAmount <= heAmount && teAmount <= meAmount && teAmount!=0)
					teAmount++;
			}
			Random random = new Random();
			int recIndex = -1;
			for(int i=0;i<heAmount;i++) {
				recIndex = random.nextInt(headEqualVec.size());
				int range = headEqualVec.get(recIndex).getCordinates().size();
				Cordinate randCor = headEqualVec.get(recIndex).getCordinates().get(random.nextInt(range - 1));
				if(gridAmount[randCor.getX()/Parameters.sigGridSize][randCor.getY()/Parameters.sigGridSize]<=3) {
					if(randCor.isUnique(corVec,randCor)) {
						corVec.add(randCor);
						gridAmount[randCor.getX()/Parameters.sigGridSize][randCor.getY()/Parameters.sigGridSize]++;
					} else i--;
				}else i--;
			}
			recIndex=-1;
			for(int j=0;j<teAmount;j++) {
				recIndex = random.nextInt(tailEqualVec.size());
				int range= tailEqualVec.get(recIndex).getCordinates().size();
				Cordinate randCor = tailEqualVec.get(recIndex).getCordinates().get(random.nextInt(range - 1));
				if(gridAmount[randCor.getX()/Parameters.sigGridSize][randCor.getY()/Parameters.sigGridSize]<=3) {
					if(randCor.isUnique(corVec,randCor)) {
						corVec.add(randCor);
						gridAmount[randCor.getX()/Parameters.sigGridSize][randCor.getY()/Parameters.sigGridSize]++;
					} else j--;
				}else j--;
			}
			recIndex=-1;
			for(int k=0;k<meAmount;k++) {
				recIndex = random.nextInt(midEqualVec.size());
				int range = midEqualVec.get(recIndex).getCordinates().size();
				Cordinate randCor = midEqualVec.get(recIndex).getCordinates().get(random.nextInt(range - 1));
				if(gridAmount[randCor.getX()/Parameters.sigGridSize][randCor.getY()/Parameters.sigGridSize]<=3) {
					if(randCor.isUnique(corVec,randCor)) {
						corVec.add(randCor);
						gridAmount[randCor.getX()/Parameters.sigGridSize][randCor.getY()/Parameters.sigGridSize]++;
					} else k--;
				}else k--;
			}
			sigRecord.setCordinates(corVec);
			return sigRecord;
		}
	}
    /**
     * @param sigRecord 需要绘画的处理好的记录
     */

	public void fillTheBrain(SigRecord sigRecord) {
		Vector<Cordinate> cordinateVec = sigRecord.getCordinates();
		Color formerColor = graphic2d.getColor();
		if(paintGrid) {
			graphic2d.setComposite(gridAlphaComposite);
			int[][] gridMap = new int [Parameters.gridMapSize][Parameters.gridMapSize];
			Cordinate cordinate = new Cordinate();
			for (Cordinate element : cordinateVec) {
				cordinate = element;
				gridMap[cordinate.getY()/Parameters.sigGridSize][cordinate.getX()/Parameters.sigGridSize]++;
			}
			for(int gX = 0;gX<Parameters.gridMapSize;gX++) {
				for(int gY = 0;gY<Parameters.gridMapSize;gY++) {
					if(gridMap[gX][gY] == 0) {
						continue;
					} else if(gridMap[gX][gY] == 1) {
						graphic2d.setColor(Color.green);
						graphic2d.fillRect(gY*Parameters.sigGridSize, gX*Parameters.sigGridSize,
								Parameters.sigGridSize, Parameters.sigGridSize);
					} else if(gridMap[gX][gY] == 2) {
						graphic2d.setColor(Color.yellow);
						graphic2d.fillRect(gY*Parameters.sigGridSize, gX*Parameters.sigGridSize,
								Parameters.sigGridSize, Parameters.sigGridSize);
					} else if(gridMap[gX][gY] == 3) {
						graphic2d.setColor(Color.orange);
						graphic2d.fillRect(gY*Parameters.sigGridSize, gX*Parameters.sigGridSize,
								Parameters.sigGridSize, Parameters.sigGridSize);
					} else if(gridMap[gX][gY] == 4) {
						graphic2d.setColor(Color.red);
						graphic2d.fillRect(gY*Parameters.sigGridSize, gX*Parameters.sigGridSize,
								Parameters.sigGridSize, Parameters.sigGridSize);
					}
				}
			}
		}
		graphic2d.setComposite(shapeAlphaComposite);//设置透明度
		graphic2d.setColor(formerColor);
		int corAmount = cordinateVec.size();
		for(int i=0;i<corAmount;i++) {
			graphic2d.fillOval(cordinateVec.get(i).getX(), cordinateVec.get(i).getY(), Parameters.shapeSize, Parameters.shapeSize);
		}
		return;
	}
	public boolean isMarkDots() {
		return markDots;
	}
	public void setMarkDots(boolean markDots) {
		this.markDots = markDots;
	}

	public int getPaintType() {
		return paintType;
	}

	public void setPaintType(int paintResult) {
		this.paintType = paintResult;
	}

	public Vector<String> getPhraseVec() {
		return phraseVec;
	}

	public void setPhraseVec(Vector<String> phraseVec) {
		this.phraseVec = phraseVec;
	}

	public Vector<SigRecord> getRecordVec() {
		return recordVec;
	}

	public void setRecordVec(Vector<SigRecord> recordVec) {
		this.recordVec = recordVec;
	}

	public boolean isPaintGrid() {
		return paintGrid;
	}

	public void setPaintGrid(boolean paintGrid) {
		this.paintGrid = paintGrid;
	}

}
