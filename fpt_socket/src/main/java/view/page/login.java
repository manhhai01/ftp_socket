/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.page;

import java.awt.Frame;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.SwingUtilities;
import payloads.DataResponse;
import socket.StatusCode;
import socket.socketManager;
import view.custom.customDialog;
import view.mainLayout;

/**
 *
 * @author Bum
 */
public class login extends javax.swing.JPanel {
    private customDialog customDialog;
    private Frame parentFrame;
    private String username=null,password=null;
    /**
     * Creates new form login
     */
    public login() {
        initComponents();
        repaint();
        createCustomdialog();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        otpVerifyPanel = new javax.swing.JPanel();
        verifyConfirm = new view.custom.Button();
        verifyCancel = new view.custom.Button();
        verifyTitle = new javax.swing.JLabel();
        verifyField = new view.custom.textField();
        regenerateBtn = new view.custom.Button();
        userField = new view.custom.textField();
        passwordField = new view.custom.passwordField();
        button1 = new view.custom.Button();

        otpVerifyPanel.setBackground(new java.awt.Color(255, 255, 255));
        otpVerifyPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 216, 216)));

        verifyConfirm.setText("Xác nhận");
        verifyConfirm.setColor(new java.awt.Color(204, 204, 255));
        verifyConfirm.setColorClick(new java.awt.Color(153, 153, 153));
        verifyConfirm.setColorOver(new java.awt.Color(102, 102, 102));
        verifyConfirm.setRadius(10);
        verifyConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyConfirmActionPerformed(evt);
            }
        });

        verifyCancel.setText("Hủy");
        verifyCancel.setColor(new java.awt.Color(204, 204, 255));
        verifyCancel.setColorClick(new java.awt.Color(153, 153, 153));
        verifyCancel.setColorOver(new java.awt.Color(102, 102, 102));
        verifyCancel.setRadius(10);
        verifyCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyCancelActionPerformed(evt);
            }
        });

        verifyTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        verifyTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        verifyTitle.setText("Xác thực OTP");

        verifyField.setLabelText("OTP");

        regenerateBtn.setText("Gửi lại mã");
        regenerateBtn.setColor(new java.awt.Color(204, 204, 204));
        regenerateBtn.setColorClick(new java.awt.Color(153, 153, 153));
        regenerateBtn.setColorOver(new java.awt.Color(102, 102, 102));
        regenerateBtn.setRadius(5);
        regenerateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regenerateBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout otpVerifyPanelLayout = new javax.swing.GroupLayout(otpVerifyPanel);
        otpVerifyPanel.setLayout(otpVerifyPanelLayout);
        otpVerifyPanelLayout.setHorizontalGroup(
            otpVerifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otpVerifyPanelLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(otpVerifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(verifyTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(otpVerifyPanelLayout.createSequentialGroup()
                        .addComponent(verifyConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(verifyCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(otpVerifyPanelLayout.createSequentialGroup()
                        .addComponent(verifyField, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(regenerateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(119, Short.MAX_VALUE))
        );
        otpVerifyPanelLayout.setVerticalGroup(
            otpVerifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(otpVerifyPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(verifyTitle)
                .addGap(27, 27, 27)
                .addGroup(otpVerifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(verifyField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(regenerateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(otpVerifyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(verifyCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(verifyConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(100, 378));
        setPreferredSize(new java.awt.Dimension(400, 220));
        setRequestFocusEnabled(false);

        userField.setLabelText("email");

        passwordField.setLabelText("Mật khẩu");

        button1.setBackground(new java.awt.Color(204, 204, 255));
        button1.setColor(new java.awt.Color(204, 204, 255));
        button1.setColorOver(new java.awt.Color(255, 204, 204));
        button1.setLabel("Đăng nhập");
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                        .addComponent(button1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(85, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(115, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        username = userField.getText();
        password = passwordField.getText();
        if(!username.isEmpty() && !password.isEmpty()){
            try {
                DataResponse dataResponse = socketManager.getInstance().login(username, password);                
                if(dataResponse.getStatus() == StatusCode.LOGGED_IN){
                    loginSuccess();
                }
                else if(dataResponse.getStatus() == StatusCode.OTP_NEEDED){
                    JOptionPane.showMessageDialog(parentFrame, "Toàn khoản chưa xác thực, hãy kiểm tra email", "Thông báo", WARNING_MESSAGE);
                    customDialog.setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(this, dataResponse.getMessage());
                }
            } catch (Exception ex) {
                Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Vui lòng không để trống thông tin!!!");
        }
    }//GEN-LAST:event_button1ActionPerformed

    private void verifyCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifyCancelActionPerformed
        customDialog.setVisible(false);
        verifyField.setText("");
    }//GEN-LAST:event_verifyCancelActionPerformed

    private void verifyConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifyConfirmActionPerformed
        String otp = verifyField.getText();
        if(!otp.isEmpty()){
            try {
                DataResponse response = socketManager.getInstance().verifyOTP(username, password, otp);
                if(response.getStatus() == StatusCode.COMMAND_OK){
                    JOptionPane.showMessageDialog(parentFrame, "Xác thực thành công!!","Success",INFORMATION_MESSAGE);
                    customDialog.setVisible(false);
                    loginSuccess();
                }else{
                    JOptionPane.showMessageDialog(this, response.getMessage(),"Thông báo",WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                Logger.getLogger(login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_verifyConfirmActionPerformed

    private void regenerateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regenerateBtnActionPerformed
        try {
            DataResponse res = socketManager.getInstance().regenerateOTP(username, password);
            if(res.getStatus()==StatusCode.COMMAND_OK){
                JOptionPane.showMessageDialog(parentFrame, "Đã gửi lại mã OTP, vui lòng check mail","Success",INFORMATION_MESSAGE);
            }
            else
            JOptionPane.showMessageDialog(parentFrame, "Có lỗi xảy ra, vui lòng thử lại","Thông báo",WARNING_MESSAGE);
        } catch (Exception ex) {
            Logger.getLogger(register.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_regenerateBtnActionPerformed
    public void createCustomdialog(){
        parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        customDialog = new customDialog(parentFrame);
        customDialog.setDialogContent(otpVerifyPanel);
    }
    public void loginSuccess() throws Exception{
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        frame.dispose();
        new mainLayout().setVisible(true);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private view.custom.Button button1;
    private javax.swing.JPanel otpVerifyPanel;
    private view.custom.passwordField passwordField;
    private view.custom.Button regenerateBtn;
    private view.custom.textField userField;
    private view.custom.Button verifyCancel;
    private view.custom.Button verifyConfirm;
    private view.custom.textField verifyField;
    private javax.swing.JLabel verifyTitle;
    // End of variables declaration//GEN-END:variables
}
