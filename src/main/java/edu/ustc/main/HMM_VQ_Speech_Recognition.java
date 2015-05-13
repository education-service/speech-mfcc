package edu.ustc.main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.ustc.audio.JSoundCapture;
import edu.ustc.db.DataBase;
import edu.ustc.db.ObjectIODataBase;
import edu.ustc.db.TrainingTestingWaveFiles;
import edu.ustc.mediator.Operations;
import edu.ustc.util.ErrorManager;

/**
 * 主类：包含GUI和主要的方法--训练/测试/数据都在这里使用
 *
 * @author wanggang
 *
 */
public class HMM_VQ_Speech_Recognition extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JSoundCapture soundCapture = null;
	private JTabbedPane jTabbedPane = null;
	private JPanel verifyPanel = null;
	private JPanel trainPanel = null;
	private JPanel runTrainingPanel = null;
	private JButton getWordButton = null;
	private JButton btnVerify = null;
	private JComboBox<String> wordsComboBoxVerify = null;
	private JComboBox<String> wordsComboBoxAddWord = null;

	private JButton getWordButton1 = null;

	private Operations opr = new Operations();
	private JLabel aboutLBL;
	private JLabel statusLBLRecognize;
	private JTextField addWordToCombo = null;
	private JButton addWordToComboBtn = null;
	private JButton addTrainSampleBtn = null;
	private JLabel lblChooseAWord;
	private JLabel lblAddANew;

	private JButton generateCodeBookBtn;
	private JButton btnNewButton_2;

	/**
	 * 默认构造函数
	 */
	public HMM_VQ_Speech_Recognition() {
		super();
		initialize();
		ErrorManager.setMessageLbl(getStatusLblRecognize());
	}

	/**
	 * 初始化方法
	 */
	private void initialize() {
		this.setSize(485, 335);
		this.setContentPane(getJContentPane());
		this.setTitle("HMM/VQ语音识别");
	}

	/**
	 * 初始化jTabbedPane
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setBounds(new Rectangle(10, 94, 449, 178));
			jTabbedPane.addTab("验证词语", null, getVerifyWordPanel(), null);
			jTabbedPane.addTab("添加样本", null, getAddSamplePanel(), null);
			jTabbedPane.addTab("运行HMM训练", null, getRunTrainingPanel(), null);
			jTabbedPane.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					System.out.println("state changed");
					if (jTabbedPane.getSelectedIndex() == 0) {
						soundCapture.setSaveFileName(null);
					} else if (jTabbedPane.getSelectedIndex() == 1) {
						soundCapture.setSaveFileName("TrainWav\\" + getWordsComboBoxAddWord().getSelectedItem() + "\\"
								+ getWordsComboBoxAddWord().getSelectedItem());
					}

				}
			});
		}
		return jTabbedPane;
	}

	private File getTestFile() {
		JFileChooser jfc = new JFileChooser("选择WAVE文件验证");
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.setSize(new Dimension(541, 326));
		jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
			@Override
			public String getDescription() {
				return ".WAV & .WAVE Files";
			}

			@Override
			public boolean accept(File f) {
				return (f.getName().toLowerCase().endsWith("wav") || f.getName().toLowerCase().endsWith("wave") || f
						.isDirectory());
			}
		});
		int chooseOpt = jfc.showOpenDialog(this);
		if (chooseOpt == JFileChooser.APPROVE_OPTION) {
			File file = jfc.getSelectedFile();
			System.out.println("selected File " + file);
			return file;
		}
		return null;
	}

	/**
	 * 初始化jPanel
	 * @return javax.swing.JPanel
	 */
	private JPanel getVerifyWordPanel() {
		if (verifyPanel == null) {
			JLabel jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(13, 55, 245, 20));
			jLabel.setText("或者...从列表中选择词语来验证");
			verifyPanel = new JPanel();
			verifyPanel.setLayout(null);
			verifyPanel.add(getGetWordButton(), null);
			verifyPanel.add(getWordsComboBoxVerify(), null);
			verifyPanel.add(jLabel, null);
			verifyPanel.add(getGetWordButton1(), null);
			verifyPanel.add(getBtnVerify());
			verifyPanel.add(getStatusLblRecognize());
		}
		return verifyPanel;
	}

	private JButton getBtnVerify() {
		if (btnVerify == null) {
			btnVerify = new JButton("验证");
			btnVerify.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (soundCapture.isSoundDataAvailable() && getWordsComboBoxVerify().getItemCount() > 0) {
						String recWord = opr.hmmGetWordFromAmplitureArray(soundCapture.getAudioData());
						if (recWord.equalsIgnoreCase(getWordsComboBoxVerify().getSelectedItem().toString())) {
							getStatusLblRecognize().setText("已经验证");
						} else {
							getStatusLblRecognize().setText("没有验证");
						}
					}
				}
			});
			btnVerify.setBounds(126, 111, 89, 24);
		}
		return btnVerify;
	}

	/**
	 * 初始化jPanel1
	 * @return javax.swing.JPanel
	 */
	private JPanel getAddSamplePanel() {
		if (trainPanel == null) {
			trainPanel = new JPanel();
			trainPanel.setLayout(null);
			trainPanel.add(getWordsComboBoxAddWord(), null);
			trainPanel.add(getAddWordToCombo(), null);
			trainPanel.add(getAddWordToComboBtn(), null);
			trainPanel.add(getLblChooseAWord());
			trainPanel.add(getLblAddANew());
			// trainPanel.add(getAddTrainSampleBtn(), null);
		}
		return trainPanel;
	}

	/**
	 * 初始化runTrainingPanel
	 * @return javax.swing.JPanel
	 */
	private JPanel getRunTrainingPanel() {
		if (runTrainingPanel == null) {
			runTrainingPanel = new JPanel();
			runTrainingPanel.setLayout(null);
			runTrainingPanel.add(getGenerateCodeBookBtn());
			runTrainingPanel.add(getBtnNewButton_2());
		}
		return runTrainingPanel;
	}

	/**
	 * 初始化getWordButton
	 * @return javax.swing.JButton
	 */
	private JButton getGetWordButton() {
		if (getWordButton == null) {
			getWordButton = new JButton("使用刚才的录音来识别");
			getWordButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (soundCapture.isSoundDataAvailable() && getWordsComboBoxVerify().getItemCount() > 0) {
						getStatusLblRecognize().setText(opr.hmmGetWordFromAmplitureArray(soundCapture.getAudioData()));

					}
				}
			});
			getWordButton.setBounds(new Rectangle(13, 8, 202, 24));
		}
		return getWordButton;
	}

	/**
	 * 初始化wordsComboBox
	 * @return javax.swing.JComboBox
	 */
	private JComboBox<String> getWordsComboBoxVerify() {
		if (wordsComboBoxVerify == null) {
			DataBase db = new ObjectIODataBase();
			db.setType("hmm");
			wordsComboBoxVerify = new JComboBox<String>();
			try {
				String[] regs = db.readRegistered();
				for (int i = 0; i < regs.length; i++) {
					wordsComboBoxVerify.addItem(regs[i]);
				}
			} catch (Exception e) {
			}
			wordsComboBoxVerify.setBounds(new Rectangle(13, 75, 202, 24));
		}
		return wordsComboBoxVerify;
	}

	private JComboBox<String> getWordsComboBoxAddWord() {
		if (wordsComboBoxAddWord == null) {
			TrainingTestingWaveFiles ttwf = new TrainingTestingWaveFiles("train");
			wordsComboBoxAddWord = new JComboBox<String>();
			try {
				String[] regs = ttwf.readWordWavFolder();
				for (int i = 0; i < regs.length; i++) {
					wordsComboBoxAddWord.addItem(regs[i]);
				}
			} catch (Exception e) {
			}
			wordsComboBoxAddWord.setBounds(new Rectangle(11, 103, 202, 24));
			wordsComboBoxAddWord.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					soundCapture.setSaveFileName("TrainWav\\" + getWordsComboBoxAddWord().getSelectedItem() + "\\"
							+ getWordsComboBoxAddWord().getSelectedItem());
				}
			});
		}
		return wordsComboBoxAddWord;
	}

	/**
	 * 初始化getWordButton1
	 * @return javax.swing.JButton
	 */
	private JButton getGetWordButton1() {
		if (getWordButton1 == null) {
			getWordButton1 = new JButton("识别一个已经保存的WAV文件");
			getWordButton1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("getting word file totest");
					File f = getTestFile();
					if (f != null)
						getStatusLblRecognize().setText(opr.hmmGetWordFromFile(f));
				}
			});
			getWordButton1.setBounds(new Rectangle(225, 8, 189, 24));
		}
		return getWordButton1;
	}

	/**
	 * 初始化jContentPane
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTabbedPane());
			jContentPane.add(getSoundCapture());
			jContentPane.add(getAboutLBL());
		}
		return jContentPane;
	}

	private JSoundCapture getSoundCapture() {
		if (soundCapture == null) {
			soundCapture = new JSoundCapture(true, true);
			soundCapture.setBounds(10, 10, 431, 74);
		}
		return soundCapture;
	}

	private JLabel getAboutLBL() {
		if (aboutLBL == null) {
			aboutLBL = new JLabel("开发者: 何长婷");
			aboutLBL.setHorizontalAlignment(SwingConstants.CENTER);
			aboutLBL.setFont(new Font("Tahoma", Font.PLAIN, 11));
			aboutLBL.setBounds(10, 275, 449, 16);
		}
		return aboutLBL;
	}

	private JLabel getStatusLblRecognize() {
		if (statusLBLRecognize == null) {
			statusLBLRecognize = new JLabel("");
			statusLBLRecognize.setHorizontalAlignment(SwingConstants.CENTER);
			statusLBLRecognize.setBounds(225, 71, 189, 68);
		}
		return statusLBLRecognize;
	}

	/**
	 * 初始化addWordToCombo
	 * @return javax.swing.JTextField
	 */
	private JTextField getAddWordToCombo() {
		if (addWordToCombo == null) {
			addWordToCombo = new JTextField();
			addWordToCombo.setBounds(new Rectangle(10, 42, 202, 24));
		}
		return addWordToCombo;
	}

	/**
	 * 初始化addWordToComboBtn
	 * @return javax.swing.JButton
	 */
	private JButton getAddWordToComboBtn() {
		if (addWordToComboBtn == null) {
			addWordToComboBtn = new JButton("添加词语");
			addWordToComboBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String newWord = getAddWordToCombo().getText();
					boolean isAlreadyRegistered = false;
					if (!newWord.isEmpty()) {
						// 已经存在combo box中
						for (int i = 0; i < getWordsComboBoxAddWord().getItemCount(); i++) {
							if (getWordsComboBoxAddWord().getItemAt(i).toString().equalsIgnoreCase(newWord)) {
								isAlreadyRegistered = true;
								break;
							}
						}
						// 没有添加
						if (!isAlreadyRegistered) {
							getWordsComboBoxAddWord().addItem(getAddWordToCombo().getText());
							getWordsComboBoxAddWord().repaint();
							getAddWordToCombo().setText("");
						}
					}
				}
			});
			addWordToComboBtn.setBounds(new Rectangle(222, 42, 142, 24));
		}
		return addWordToComboBtn;
	}

	/**
	 * 初始化addTrainSample
	 * @return javax.swing.JButton
	 */
	public JButton getAddTrainSampleBtn() {
		if (addTrainSampleBtn == null) {
			addTrainSampleBtn = new JButton("录音");
			addTrainSampleBtn.setBounds(new Rectangle(223, 103, 141, 24));
			addTrainSampleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (addTrainSampleBtn.getText().startsWith("Record")) {
						soundCapture.startRecord();
						addTrainSampleBtn.setText("Save Captured");
					} else if (addTrainSampleBtn.getText().startsWith("Save")) {
						// TODO: decouple path, may be singleton conf for path
						soundCapture.setSaveFileName("TrainWav\\" + getWordsComboBoxAddWord().getSelectedItem() + "\\"
								+ getWordsComboBoxAddWord().getSelectedItem());
						soundCapture.getFileNameAndSaveFile();
						addTrainSampleBtn.setText("Record");
					}
				}
			});
		}
		return addTrainSampleBtn;
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				HMM_VQ_Speech_Recognition test = new HMM_VQ_Speech_Recognition();
				test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				test.setResizable(false);
				test.setVisible(true);
			}
		});
	}

	private JLabel getLblChooseAWord() {
		if (lblChooseAWord == null) {
			lblChooseAWord = new JLabel("选择一个词语录音并保存到相应的目录");
			lblChooseAWord.setBounds(11, 77, 325, 14);
		}
		return lblChooseAWord;
	}

	private JLabel getLblAddANew() {
		if (lblAddANew == null) {
			lblAddANew = new JLabel("增加一个新词语");
			lblAddANew.setBounds(11, 11, 126, 14);
		}
		return lblAddANew;
	}

	private JButton getGenerateCodeBookBtn() {
		if (generateCodeBookBtn == null) {
			generateCodeBookBtn = new JButton("生成CodeBook");
			generateCodeBookBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					opr.generateCodebook();
				}
			});
			generateCodeBookBtn.setBounds(10, 32, 167, 23);
		}
		return generateCodeBookBtn;
	}

	private JButton getBtnNewButton_2() {
		if (btnNewButton_2 == null) {
			btnNewButton_2 = new JButton("训练HMM");
			btnNewButton_2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					opr.hmmTrain();
				}
			});
			btnNewButton_2.setBounds(10, 74, 167, 23);
		}
		return btnNewButton_2;
	}

}
