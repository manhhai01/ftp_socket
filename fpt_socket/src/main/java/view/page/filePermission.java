/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.page;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.SwingUtilities;
import payloads.UserPermission;
import socket.StatusCode;
import socket.socketManager;
import view.page.ShareOptionPane;

/**
 *
 * @author Son
 */
public class filePermission extends javax.swing.JPanel {
        private String filename,username;
    private String readable,writable;
    /**
     * Creates new form NewJPanel
     */
    public filePermission(UserPermission userPermission,String filename) {
        initComponents();
        this.filename = filename;
        username = userPermission.getUserData().getUsername();
        String fullname = userPermission.getUserData().getLastName() + " " + userPermission.getUserData().getFirstName();
        HashMap<String,Object> permission = userPermission.getProcessedPermission();
        String permissionString =(String) permission.get("permission");
        if(permissionString.equals("r"))
            permissionOption.setSelectedIndex(0);
        else
            permissionOption.setSelectedIndex(1);
        fullnameLbl.setText(fullname);
        usernameLbl.setText(username);
        permissionOption.setEnabled(true);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fullnameLbl = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        usernameLbl = new javax.swing.JLabel();
        permissionOption = new javax.swing.JComboBox<>();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        fullnameLbl.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        fullnameLbl.setText("<Name>");

        jLabel2.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
        jLabel2.setText("X");
        jLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel2MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jLabel2MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jLabel2MouseExited(evt);
            }
        });

        usernameLbl.setText("<username>");

        permissionOption.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chỉ xem", "Chỉnh sửa" }));
        permissionOption.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                permissionOptionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fullnameLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usernameLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addComponent(permissionOption, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jLabel2)
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(fullnameLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameLbl)
                .addGap(12, 12, 12))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(permissionOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel2MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseEntered
        jLabel2.setForeground(new java.awt.Color(255,153,153));
    }//GEN-LAST:event_jLabel2MouseEntered

    private void jLabel2MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseExited
        jLabel2.setForeground(new java.awt.Color(255, 0, 0));
    }//GEN-LAST:event_jLabel2MouseExited

    private void jLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel2MouseClicked
        jLabel2.setForeground(new java.awt.Color(255,51,51));
        try {
            ShareOptionPane parentFrame = (ShareOptionPane) SwingUtilities.getWindowAncestor(this);
            if(socketManager.getInstance().deletePermission("file", filename, username).getStatus() == StatusCode.FILE_ACTION_NOT_TAKEN){
                JOptionPane.showMessageDialog(parentFrame, "Có lỗi xảy ra, tiến hành cập nhật lại danh sách!", "Thông báo", WARNING_MESSAGE);
            }
            parentFrame.refreshContent();
        } catch (Exception ex) {
            Logger.getLogger(folderPermission.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jLabel2MouseClicked

    private void permissionOptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_permissionOptionActionPerformed
        if(permissionOption.isEnabled())    
        try {
                changePermission();
            } catch (Exception ex) {
                Logger.getLogger(filePermission.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_permissionOptionActionPerformed
    public void changePermission() throws Exception {
        String sql="file "+filename+" "+username+
                " "+(permissionOption.getSelectedIndex()==0?"r":"w")
                ;
        System.out.println(sql);
        String permission = permissionOption.getSelectedIndex()==0?"r":"w";
        if(socketManager.getInstance().grantFilePermission(filename, username,permission).getStatus() == StatusCode.FILE_ACTION_NOT_TAKEN){
            ShareOptionPane parentFrame = (ShareOptionPane) SwingUtilities.getWindowAncestor(this);
            JOptionPane.showMessageDialog(parentFrame, "Có lỗi xảy ra", "Thông báo", WARNING_MESSAGE);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel fullnameLbl;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox<String> permissionOption;
    private javax.swing.JLabel usernameLbl;
    // End of variables declaration//GEN-END:variables
}
