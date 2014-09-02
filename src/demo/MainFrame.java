/*
 * Copyright 2014 aarestu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo;

import grayscalemultithreading.GrayScale;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author aarestu
 */
public class MainFrame extends JFrame {
    private final JLabel labelImage;
    private final JLabel labelWaktu;
    private final JButton button;
    private final GrayScale gs = new GrayScale();
    
    public MainFrame(String title) {
        super(title);
        
        setLayout(new BorderLayout());
        
        labelImage = new JLabel();
        labelWaktu = new JLabel();
        button = new JButton("Browse");
        
        labelImage.setAutoscrolls(true);
        
        Container c = getContentPane();
        c.add(labelWaktu, BorderLayout.NORTH);
        c.add(labelImage, BorderLayout.CENTER);
        c.add(button, BorderLayout.SOUTH);
        
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                browse();
            }
        });
    }
    
    private void browse(){
        JFileChooser fc = new JFileChooser("Buka File");
        FileFilter filter = new FileNameExtensionFilter("Image", "jpg", "jpeg", "png", "gif");
        fc.setFileFilter(filter);
        
        int returnVal = fc.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                
                long start = System.currentTimeMillis();
                
                Image img = gs.convert(ImageIO.read(file), GrayScale.GRAY_DESATURATION, 8);
                
                long end = System.currentTimeMillis();
                
                labelWaktu.setText("Waktu execute : " + (end - start) + " milidetik");
                labelImage.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                labelImage.setText(ex.getMessage());
            }
        }
    }
}
