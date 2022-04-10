package common;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

/**
 * ���ڶ�ͼƬ����45����Ϊ�����10*10���񻯴����õ���Ҫ������
 * @author �����
 *
 */
public class ImageGrid {
	/*
	 * ����RGB��ɫ�ռ䷶Χ���л���
	 * ���������ȫ���˵���ֱ�ӿ�������ֿ�
	 */
	int BLACK_RANGE = 20;
	private int[][] gridMap = new int[Parameters.gridMapSize][Parameters.gridMapSize];//�������մ洢��ǰ������鱻����ʲôɫ�顣0����Ч/4����/3����/2����/1����
	enum ColorRange{RED,ORANGE,YELLOW,GREEN}//��ͬ�ķ�Χ����ָʾ�������������ɸ���
	private Vector<Cordinate> redCorVec;//��ź�ɫ��������
	private Vector<Cordinate> orangeCorVec;//��ų�ɫ��������
	private Vector<Cordinate> yellowCorVec;//��Ż�ɫ��������
	private Vector<Cordinate> greenCorVec;//�����ɫ��������
	public ImageGrid() {}
	/**
	 * ���캯��
	 * @param imagePath ͼƬ��·��,��ҪΪ����·��
	 */
	public ImageGrid(String imagePath) {
		redCorVec = new Vector<>();
		orangeCorVec = new Vector<>();
		yellowCorVec = new Vector<>();
		greenCorVec = new Vector<>();
		int[] rgb = new int[3];
        File file = new File(imagePath);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        ColorRange colorRange = null;
        for(int gX = 0;gX<Parameters.gridMapSize;gX++) {
        	for(int gY=0;gY<Parameters.gridMapSize;gY++) {
        		int redAmount = 0;
        		int orangeAmount = 0;
        		int yellowAmount = 0;
        		int greenAmount = 0;
        		int invalidAmount = 0;
        		for (int i = gY*Parameters.sigGridSize; i < gY*Parameters.sigGridSize + Parameters.sigGridSize; i++) {
        	          for (int j = gX*Parameters.sigGridSize; j < gX*Parameters.sigGridSize + Parameters.sigGridSize; j++) {
        	              int pixel = bufferedImage.getRGB(i, j); // �������д��뽫һ������ת��ΪRGB����
        	              rgb[0] = (pixel & 0xff0000) >> 16;
        	              rgb[1] = (pixel & 0xff00) >> 8;
        	              rgb[2] = (pixel & 0xff);
        	              if(isValid(rgb)) {
        	            	  colorRange = getRange(rgb);
        	            	  if(colorRange == ColorRange.RED) {
        	            		  redAmount++;
        	            	  } else if(colorRange == ColorRange.ORANGE) {
        	            		  orangeAmount++;
        	            	  } else if(colorRange == ColorRange.YELLOW) {
        	            		  yellowAmount++;
        	            	  } else if(colorRange == ColorRange.GREEN) {
        	            		  greenAmount++;
        	            	  }
        	              } else {
        	            	  invalidAmount++;
        	              }
        	          }
        		}
        		if(invalidAmount > Parameters.invalidAmount) gridMap[gX][gY] = 0;
        		else {
        			gridMap[gX][gY] = calGridType(redAmount,
        					orangeAmount,yellowAmount,greenAmount);
        		}
        	}
        }
        for(int i=0;i<Parameters.gridMapSize;i++) {
        	for(int j=0;j<Parameters.gridMapSize;j++) {
        		int corAmount = gridMap[i][j];
        		for(int k=0;k<corAmount;k++) {
        			Cordinate cordinate = new Cordinate();
        			Random random = new Random();
        			cordinate.setCordinate(random.nextInt(Parameters.sigGridSize)+j*Parameters.sigGridSize, random.nextInt(Parameters.sigGridSize)+i*Parameters.sigGridSize);
        			if(corAmount==1) {
        				greenCorVec.add(cordinate);
        			} else if(corAmount == 2) {
        				yellowCorVec.add(cordinate);
        			} else if(corAmount == 3) {
        				orangeCorVec.add(cordinate);
        			} else if(corAmount==4) {
        				redCorVec.add(cordinate);
        			}
        		}
        	}
        }
	}
    /**
     * ͨ������ͳ�����Ĵ�С���������������ɫƫ���ı�
     * @param redAmount ��ɫ��������
     * @param orangeAmount ��ɫ��������
     * @param yellowAmount ��ɫ��������
     * @param greenAmount ��ɫ��������
     * @return �������ո��������ɫ��Ӧֵ��Խ��ֵԽ��
     */
	private int calGridType(int redAmount, int orangeAmount, int yellowAmount,int greenAmount) {
		int biggerOne=0;
		int gridType=-1;
		if(redAmount > biggerOne) {
			biggerOne = redAmount;
			gridType = 4;
		}
		if(orangeAmount > biggerOne) {
			biggerOne = orangeAmount;
			gridType = 3;
		}
		if(yellowAmount > biggerOne) {
			biggerOne = yellowAmount;
			gridType = 2;
		}
		if(greenAmount > biggerOne) {
			biggerOne = greenAmount;
			gridType = 1;
		}
		return gridType;
	}
	/**
	 * �����жϵ�ǰ��rgb����ǲ���ɫ�ʽ�Ϊ�����ģ������ں����������Ч����
	 * @param rgb ����Ԫ�ص����飬����ָʾr,g,b��ֵ
	 * @return ��Чʱ����Ϊ��
	 */
	private boolean isValid(int[] rgb) {
		if(rgb[0] > BLACK_RANGE && rgb[1] > BLACK_RANGE && rgb[2] > BLACK_RANGE &&
				rgb[0] < 230 && rgb[1] < 230 && rgb[2] < 230) return true;
		else return false;
	}
	/**
	 * �����жϵ�ǰ��RGBɫ������˭�ķ�Χ֮�ڡ�
	 * @param rgb �����жϵ�RGBԪ������
	 * @return �����жϵ�����
	 */
	private ColorRange getRange(int[] rgb) {
		ColorRange colorRange = null;
		if(rgb[0] >= BLACK_RANGE && rgb[1] < 115 && rgb[0]>=rgb[1]) colorRange = ColorRange.RED;
		else if(rgb[0] >= rgb[1] && rgb[1] >= 115 && rgb[1] < 180) colorRange = ColorRange.ORANGE;
		else if(rgb[0] >= 200 && rgb[1]>= 180) colorRange = ColorRange.YELLOW;
		else if(rgb[0] < 200 && rgb[1] >= BLACK_RANGE) colorRange = ColorRange.GREEN;
		return colorRange;
	}

	/**
	 * ���ڽ�ͼƬ�м���������������갴����Ҫ�����ٴ���
	 * ��ѭ�Ӹߵ��͵Ĵ���˳��
	 * �� ��ǰ��������������ʣ�����裬�����ѡȡ�㹻������
	 * ��֮ȫ��¼��
	 * @param size ������Ҫ������
	 * @return requiredCorVec �������������������
	 */
	public Vector<Cordinate> getNormalizedColorVec(int size) {
		if(size>Parameters.corAmount) return null;
		Vector<Cordinate> requiredCorVec=new Vector<>();
		if(size >= redCorVec.size()) {
			for(int i=0;i<redCorVec.size();i++) {
				requiredCorVec.add(redCorVec.get(i));
			}
			size -= redCorVec.size();
			if(size>=orangeCorVec.size()) {
				for(int i=0;i<orangeCorVec.size();i++) {
					requiredCorVec.add(orangeCorVec.get(i));
				}
				size -= orangeCorVec.size();
				if(size>yellowCorVec.size()) {
					for(int i=0;i<yellowCorVec.size();i++) {
						requiredCorVec.add(yellowCorVec.get(i));
					}
					size-=yellowCorVec.size();
					if(size>greenCorVec.size()) {
						for(int i=0;i<greenCorVec.size();i++) {
							requiredCorVec.add(greenCorVec.get(i));
						}
					} else {
						for(int i=0;i<size;i++) {
							Random random = new Random();
							requiredCorVec.add(greenCorVec.get(random.nextInt(greenCorVec.size())));
						}
					}
				} else{
					for(int i=0;i<size;i++) {
						Random random = new Random();
						requiredCorVec.add(yellowCorVec.get(random.nextInt(yellowCorVec.size())));
					}
				}
			} else {
				for(int i=0;i<size;i++) {
					Random random = new Random();
					requiredCorVec.add(orangeCorVec.get(random.nextInt(orangeCorVec.size())));
				}
			}
		} else{
			for(int i=0;i<size;i++) {
				Random random = new Random();
				requiredCorVec.add(redCorVec.get(random.nextInt(redCorVec.size())));
			}
		}
		return requiredCorVec;
	}
	public int[][] getGridMap() {
		return gridMap;
	}
	public void setGridMap(int[][] gridMap) {
		this.gridMap = gridMap;
	}
	public void printGridMap() {
		int total=0;

		for(int i=0;i<Parameters.gridMapSize;i++) {
			for(int j = 0;j<Parameters.gridMapSize;j++) {
				System.out.print(gridMap[i][j] + " ");
				total+=gridMap[i][j];
			}
			System.out.println();
		}
		System.out.println("in total: " + total);
		System.out.println("red vec size: " + redCorVec.size());
		System.out.println("orange vec size: " + orangeCorVec.size());
		System.out.println("yellow vec size: " + yellowCorVec.size());
		System.out.println("green vec size " + greenCorVec.size());
	}
}
