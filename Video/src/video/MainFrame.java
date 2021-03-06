/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package video;

import com.xuggle.xuggler.demos.VideoImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author goryunov
 */
public class MainFrame extends javax.swing.JFrame {

    private Foo model = new Foo();
    private Hopfield net;
    
    private MainFrame frame = this;
    
    private boolean active = true;
    
    public void setActive() {
        //active = true;
        //buttonFileChooser.setEnabled(true);
        setState(true);
    }
    
    public void setPassive() {
        //active = false;
        setState(false);
    }
    
    private void setState(boolean state) {
        buttonFileChooser.setEnabled(state);
        buttonStart.setEnabled(state);
        buttonSimpleView.setEnabled(state);
        /*fcMinus.setEnabled(state);
        fcPlus.setEnabled(state);
        fpsMinus.setEnabled(state);
        fpsPlus.setEnabled(state);
        colLimMinus.setEnabled(state);
        colLimPlus.setEnabled(state);   */     
    }
    
    public boolean isSelectedRButtonFonModel() {
        return rbuttonFonModel.isSelected();
    }
    
    public boolean isSelectedRButtonPorogBit() {
        return rbuttonPorogBit.isSelected();
    }
    
    public boolean isSelectedRButtonPorogColor() {
        return rbuttonPorogColor.isSelected();
    }
    
    public boolean isSelectedRButtonPorogGrey() {
        return rbuttonPorogGrey.isSelected();
    }
    
    public boolean isSelectedRButtonPorogColorSubstr() {
        return rbuttonPorogColorSubstr.isSelected();
    }
    
    public boolean isSelectedRButtonItog() {
        return rbuttonItog.isSelected();
    }
    
    public boolean isSelectedSmenFon() {
        return checkboxSmenFon.isSelected();
    }
    
    public boolean isSelectedNoBorderFigures() {
        return checkboxNoBorderFigures.isSelected();
    }
    
    public double getSmenFonLimValue() {
        return Double.parseDouble(smenFonLimit.getText());
    }
    
    public int getDistSkleValue() {
        return Integer.parseInt(distSkle.getText());
    }
    
    public double getFpsValue() {
        return Integer.parseInt(fps.getText());
    }
    
    public double getFramesCountValue() {
        return Integer.parseInt(framesCount.getText());
    }
    
    public double getColorLimitValue() {
        return Integer.parseInt(colorLimit.getText());
    }
    
    public JTextField getSmenFonLim() {
        return smenFonLimit;
    }
    
    public boolean getSkleValue() {
        return checkboxSkle.isSelected();
    }
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        
        /*Integer a = 127;
        Integer b = 127;
        Integer c = 128;
        Integer d = 128;
        System.out.println(a==b);
        System.out.println(c==d);*/
        
        initComponents();
        net = new Hopfield(10, 12);
        net.init();
        net.learn();
        
        
        /*int[][] figure = new int[5][5];
        for (int i=0; i<5; i++)
            for (int j=0; j<5; j++)
                figure[i][j]=-1;
        
        figure[1][3]=1;*/
        
        /*int[][] figure = {
                            {-1, -1, -1, -1, -1}, 
                            {-1, -1, -1, -1, -1},
                            {-1, -1, -1, -1, -1},
                            {-1, -1, -1, -1, -1},
                            {-1, -1, -1, -1, -1}};
        
        figure = net.identify(figure);
        
        for (int i=0; i<5; i++) {
            java.lang.System.out.println();
            for (int j=0; j<5; j++)
                java.lang.System.out.print(figure[i][j]>=0?"+"+figure[i][j]+" ":figure[i][j]+" ");
        }*/
        
        /*int[][] test_fig = new int[7][7];
        for (int i=0; i<7; i++)
            for (int j=0; j<7; j++)
                test_fig[i][j] = -1;
        test_fig[1][1] = 1;
        test_fig[2][2] = 1;
        test_fig[3][3] = 1;
        test_fig[4][4] = 1;
        test_fig[5][5] = 1;
        test_fig[6][6] = 1;
        test_fig[1][1] = 1;
        test_fig[3][4] = 1;
        test_fig[4][3] = 1;
        test_fig[4][1] = 1;
        
        java.lang.System.out.println(net.getWidth()+" "+net.getHeight());
        
        java.lang.System.out.println("\nИзначально");
        
        for (int i=0; i<7; i++) {
            for (int j=0; j<7; j++) {
                //java.lang.System.out.println(i+" "+j);
                if (j==0)
                    java.lang.System.out.println();
                if (j>0)
                    java.lang.System.out.print(".");
                java.lang.System.out.print(test_fig[i][j]==1?"#":"_");
            }
        }
        
        int[][] res = net.resize(test_fig, 7, 7);
        
        java.lang.System.out.println("\n\n\nДо трансформации");
        
        for (int i=0; i<net.getWidth(); i++) {
            for (int j=0; j<net.getHeight(); j++) {
                if (j==0)
                    java.lang.System.out.println();
                if (j>0)
                    java.lang.System.out.print(".");
                java.lang.System.out.print(res[i][j]==1?"#":"_");
            }
        }
        
        res = net.identify(res);
        
        java.lang.System.out.println("\n\n\nПосле трансформации");
        
        for (int i=0; i<net.getWidth(); i++) {
            for (int j=0; j<net.getHeight(); j++) {
                if (j==0)
                    java.lang.System.out.println();
                if (j>0)
                    java.lang.System.out.print(".");
                java.lang.System.out.print(res[i][j]==1?"#":"_");
            }
        }*/
        //net.test_old();
        //net.test();
        buttonGroup.add(rbuttonFonModel);
        buttonGroup.add(rbuttonPorogBit);
        buttonGroup.add(rbuttonPorogColor);
        buttonGroup.add(rbuttonPorogGrey);
        buttonGroup.add(rbuttonPorogColorSubstr);
        buttonGroup.add(rbuttonItog);
        //rbuttonFonModel.setSelected(true);
        //rbuttonPorogBit.setSelected(true);
        rbuttonItog.setSelected(true);
        model.setNet(net);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup = new javax.swing.ButtonGroup();
        buttonFileChooser = new javax.swing.JButton();
        buttonStart = new javax.swing.JButton();
        loadingLabel = new javax.swing.JLabel();
        buttonSimpleView = new javax.swing.JButton();
        panel = new video.VideoPanel();
        stopButton = new javax.swing.JButton();
        generated = new video.VideoPanel();
        fps = new javax.swing.JTextField();
        fpsMinus = new javax.swing.JButton();
        fpsPlus = new javax.swing.JButton();
        fcMinus = new javax.swing.JButton();
        framesCount = new javax.swing.JTextField();
        fcPlus = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        colorLimit = new javax.swing.JTextField();
        colLimPlus = new javax.swing.JButton();
        colLimMinus = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        rbuttonFonModel = new javax.swing.JRadioButton();
        rbuttonPorogBit = new javax.swing.JRadioButton();
        rbuttonPorogColor = new javax.swing.JRadioButton();
        rbuttonPorogGrey = new javax.swing.JRadioButton();
        checkboxSmenFon = new javax.swing.JCheckBox();
        smenFonLimit = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        rbuttonPorogColorSubstr = new javax.swing.JRadioButton();
        rbuttonItog = new javax.swing.JRadioButton();
        checkboxNoBorderFigures = new javax.swing.JCheckBox();
        distSkle = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        checkboxSkle = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Видео в символы");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        buttonFileChooser.setText("Выбрать файл");
        buttonFileChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonFileChooserActionPerformed(evt);
            }
        });

        buttonStart.setText("Обработать");
        buttonStart.setEnabled(false);
        buttonStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonStartActionPerformed(evt);
            }
        });

        loadingLabel.setText(" ");

        buttonSimpleView.setText("Обычный просмотр");
        buttonSimpleView.setEnabled(false);
        buttonSimpleView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSimpleViewActionPerformed(evt);
            }
        });

        panel.setPreferredSize(new java.awt.Dimension(480, 320));
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );

        stopButton.setText("Стоп");
        stopButton.setEnabled(false);
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        generated.setPreferredSize(new java.awt.Dimension(480, 320));

        javax.swing.GroupLayout generatedLayout = new javax.swing.GroupLayout(generated);
        generated.setLayout(generatedLayout);
        generatedLayout.setHorizontalGroup(
            generatedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
        );
        generatedLayout.setVerticalGroup(
            generatedLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 320, Short.MAX_VALUE)
        );

        fps.setText("1");

        fpsMinus.setText("-");
        fpsMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fpsMinusActionPerformed(evt);
            }
        });

        fpsPlus.setText("+");
        fpsPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fpsPlusActionPerformed(evt);
            }
        });

        fcMinus.setText("-");
        fcMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fcMinusActionPerformed(evt);
            }
        });

        framesCount.setText("10");

        fcPlus.setText("+");
        fcPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fcPlusActionPerformed(evt);
            }
        });

        jLabel1.setText("каждый");

        jLabel2.setText("всего");

        colorLimit.setText("30");

        colLimPlus.setText("+");
        colLimPlus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colLimPlusActionPerformed(evt);
            }
        });

        colLimMinus.setText("-");
        colLimMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colLimMinusActionPerformed(evt);
            }
        });

        jLabel4.setText("предел по цвету");

        rbuttonFonModel.setText("просто модель фона");
        rbuttonFonModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbuttonFonModelActionPerformed(evt);
            }
        });

        rbuttonPorogBit.setText("пороговая обработка ч/б");
        rbuttonPorogBit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbuttonPorogBitActionPerformed(evt);
            }
        });

        rbuttonPorogColor.setText("п.о. в цвете");
        rbuttonPorogColor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbuttonPorogColorActionPerformed(evt);
            }
        });

        rbuttonPorogGrey.setText("п.о. серым");
        rbuttonPorogGrey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbuttonPorogGreyActionPerformed(evt);
            }
        });

        checkboxSmenFon.setSelected(true);
        checkboxSmenFon.setText("слежение за сменой фона");
        checkboxSmenFon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxSmenFonActionPerformed(evt);
            }
        });

        smenFonLimit.setText("2");

        jLabel5.setText("обратный предел по смене фона");

        rbuttonPorogColorSubstr.setText("в разностном цвете");

        rbuttonItog.setText("итог");

        checkboxNoBorderFigures.setSelected(true);
        checkboxNoBorderFigures.setText("не учитывать граничные фигуры");

        distSkle.setText("5");

        jLabel6.setText("Дистанция склейки (в пикс.)");

        checkboxSkle.setText("склеивать фигуры");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(buttonFileChooser)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(buttonStart))
                                .addComponent(loadingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(checkboxNoBorderFigures))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonSimpleView)
                                .addGap(10, 10, 10)
                                .addComponent(stopButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fpsMinus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fps, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fpsPlus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fcMinus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(framesCount, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fcPlus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(colLimMinus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(colorLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(colLimPlus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(checkboxSkle)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(distSkle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(58, 58, 58)
                                                .addComponent(rbuttonFonModel))
                                            .addGroup(layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel6)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(rbuttonPorogBit)
                                            .addComponent(rbuttonItog))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(rbuttonPorogColorSubstr)
                                                .addGap(17, 17, 17)
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(smenFonLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(rbuttonPorogColor)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(rbuttonPorogGrey)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(checkboxSmenFon)))))
                                .addContainerGap())))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(generated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonFileChooser)
                    .addComponent(buttonStart)
                    .addComponent(buttonSimpleView)
                    .addComponent(stopButton)
                    .addComponent(fps, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fpsMinus)
                    .addComponent(fpsPlus)
                    .addComponent(fcMinus)
                    .addComponent(framesCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fcPlus)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(colorLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(colLimPlus)
                    .addComponent(colLimMinus)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadingLabel)
                    .addComponent(rbuttonFonModel)
                    .addComponent(rbuttonPorogBit)
                    .addComponent(rbuttonPorogColor)
                    .addComponent(rbuttonPorogGrey)
                    .addComponent(checkboxSmenFon))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(smenFonLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(rbuttonPorogColorSubstr)
                    .addComponent(rbuttonItog)
                    .addComponent(checkboxNoBorderFigures)
                    .addComponent(distSkle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkboxSkle)
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*public void paint(Graphics g) {
	g.setColor(Color.RED);

        g.fillRect(0, 0, getWidth(), getHeight());
    }*/
    
    private void buttonFileChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonFileChooserActionPerformed
        if (active==false)
            return;
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        //chooser.addChoosableFileFilter(new FileChooserFilter());
        chooser.setAcceptAllFileFilterUsed(true);
        chooser.setDialogTitle("Открыть ...");

        loadingLabel.setText("Проверка файла...");
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                model.fileChecker(chooser.getSelectedFile());
                this.repaint();
                loadingLabel.setText("Файл подходит для обработки");
                buttonStart.setEnabled(true);
                buttonSimpleView.setEnabled(true);
            } catch (Exception e) {
                loadingLabel.setText("Ошибка формата файла");
                buttonStart.setEnabled(false);
                buttonSimpleView.setEnabled(false);
                JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE); 
            }
        } else {
            loadingLabel.setText("");
        }
        //net.test_old();
        //net.test();
    }//GEN-LAST:event_buttonFileChooserActionPerformed

    private void buttonStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonStartActionPerformed
        if (active==false)
            return;
        try {
            //model.process();
            //model.processview(panel, generated, frame);
            loadingLabel.setText("Обработка...");
            
            class HelloThread extends Thread {
                public void run() {
                    stopButton.setEnabled(true);
                    frame.repaint();
                    //net.setSize(7, 7);
                    net.init();
                    net.learn();
                    model.processview(panel, generated, frame, Integer.parseInt(fps.getText()), Integer.parseInt(framesCount.getText()), Integer.parseInt(colorLimit.getText()));
                    loadingLabel.setText("Обработка файла завершена!");
                    stopButton.setEnabled(false);
                }
            }
            Thread t = new HelloThread();
            t.start();
        } catch (Exception e) {
                loadingLabel.setText("Обработка файла завершилась ошибкой");
                JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);           
                setActive();
        }
    }//GEN-LAST:event_buttonStartActionPerformed

    private void buttonSimpleViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSimpleViewActionPerformed
        if (active==false)
            return;
        try {
            loadingLabel.setText("");
            //model.simpleview(panel);
            
            class HelloThread extends Thread {

                public void run() {
                    //System.out.println("Hello, world!");
                    stopButton.setEnabled(true);
                    frame.repaint();
                    model.simpleview(panel, frame);
                    stopButton.setEnabled(false);
                }
            }
    


                Thread t = new HelloThread();
                t.start();
                /*try {
                    t.join();
                } catch (InterruptedException e) {
                    System.err.println("errrrrrr");
                }*/

        } catch (Exception e) {
                loadingLabel.setText("Просмотр файла завершился ошибкой");
                JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);   
                setActive();
        }
        //openJavaWindow();
            //new org.jdesktop.swingx.JXDialog(this, new JPanel()).setVisible(true);
    }//GEN-LAST:event_buttonSimpleViewActionPerformed

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        model.stop();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        model.stop();
    }//GEN-LAST:event_formWindowClosing

    private void panelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMouseClicked

      /*BufferedImage img = new BufferedImage(panel.getWidth(),
      panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2d = img.createGraphics();
      //g2d.set
      //panel.paint(g2d);
      g2d = (Graphics2D)panel.getGraphics();
      g2d.dispose();*/
        /*panel.repaint();
        BufferedImage img = panel.getBufferedImage();
        if (img==null)
            return;
      Raster raster = img.getRaster();
      ColorModel model = img.getColorModel();
      Object data = raster.getDataElements(evt.getX(), evt.getY(), null);
      java.lang.System.out.println(model.getRGB(data));
      int argb = model.getRGB(data);
      Color color = new Color(argb, true);

      StringBuffer message =  new StringBuffer("Color ");
      message.append("[A:").append(color.getAlpha()).append(",");
      message.append("R:").append(color.getRed()).append(",");
      message.append("G:").append(color.getGreen()).append(",");
      message.append("B:").append(color.getBlue()).append("]");
      JOptionPane.showMessageDialog(null, message.toString());

      panel.repaint();*/
    }//GEN-LAST:event_panelMouseClicked

    private void fpsMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fpsMinusActionPerformed
        int old = Integer.parseInt(fps.getText());
        if (old<2)
            return;
        fps.setText(""+(old-1));
    }//GEN-LAST:event_fpsMinusActionPerformed

    private void fpsPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fpsPlusActionPerformed
        fps.setText(""+(Integer.parseInt(fps.getText())+1));
    }//GEN-LAST:event_fpsPlusActionPerformed

    private void fcMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fcMinusActionPerformed
        int old = Integer.parseInt(framesCount.getText());
        if (old<2)
            return;
        framesCount.setText(""+(old-1));
    }//GEN-LAST:event_fcMinusActionPerformed

    private void fcPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fcPlusActionPerformed
        framesCount.setText(""+(Integer.parseInt(framesCount.getText())+1));
    }//GEN-LAST:event_fcPlusActionPerformed

    private void colLimMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colLimMinusActionPerformed
        int old = Integer.parseInt(colorLimit.getText());
        if (old<2)
            return;
        colorLimit.setText(""+(old-1));
    }//GEN-LAST:event_colLimMinusActionPerformed

    private void colLimPlusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colLimPlusActionPerformed
        colorLimit.setText(""+(Integer.parseInt(colorLimit.getText())+1));
    }//GEN-LAST:event_colLimPlusActionPerformed

    private void rbuttonFonModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbuttonFonModelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbuttonFonModelActionPerformed

    private void rbuttonPorogBitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbuttonPorogBitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbuttonPorogBitActionPerformed

    private void rbuttonPorogColorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbuttonPorogColorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbuttonPorogColorActionPerformed

    private void rbuttonPorogGreyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbuttonPorogGreyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbuttonPorogGreyActionPerformed

    private void checkboxSmenFonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxSmenFonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_checkboxSmenFonActionPerformed
  
    /*private static VideoImage mScreen = null;
    
    private static void openJavaWindow() {
        mScreen = new VideoImage();
    }*/
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonFileChooser;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JButton buttonSimpleView;
    private javax.swing.JButton buttonStart;
    private javax.swing.JCheckBox checkboxNoBorderFigures;
    private javax.swing.JCheckBox checkboxSkle;
    private javax.swing.JCheckBox checkboxSmenFon;
    private javax.swing.JButton colLimMinus;
    private javax.swing.JButton colLimPlus;
    private javax.swing.JTextField colorLimit;
    private javax.swing.JTextField distSkle;
    private javax.swing.JButton fcMinus;
    private javax.swing.JButton fcPlus;
    private javax.swing.JTextField fps;
    private javax.swing.JButton fpsMinus;
    private javax.swing.JButton fpsPlus;
    private javax.swing.JTextField framesCount;
    private video.VideoPanel generated;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel loadingLabel;
    private video.VideoPanel panel;
    private javax.swing.JRadioButton rbuttonFonModel;
    private javax.swing.JRadioButton rbuttonItog;
    private javax.swing.JRadioButton rbuttonPorogBit;
    private javax.swing.JRadioButton rbuttonPorogColor;
    private javax.swing.JRadioButton rbuttonPorogColorSubstr;
    private javax.swing.JRadioButton rbuttonPorogGrey;
    private javax.swing.JTextField smenFonLimit;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
