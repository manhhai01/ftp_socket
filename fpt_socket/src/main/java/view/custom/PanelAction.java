package view.custom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 *
 * @author RAVEN
 */
public class PanelAction extends javax.swing.JPanel {

    customMenuItem renameOption, moveOption, copyOption, deleteOption, downloadOption, shareOption;

    /**
     * Creates new form PanelAction
     */
    public PanelAction() {
        initComponents();
        initOpionMenu();

    }

    public void initOpionMenu() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/view/img/edit.png")); // Thay đổi đường dẫn tới hình ảnh của bạn
        JPanel separatorPanel = new JPanel();
        separatorPanel.setPreferredSize(new Dimension(10, 2)); // Điều chỉnh kích thước dọc và ngang
        separatorPanel.setBackground(new java.awt.Color(204, 204, 204));
        JPanel separatorPanel2 = new JPanel();
        separatorPanel2.setPreferredSize(new Dimension(10, 2)); // Điều chỉnh kích thước dọc và ngang
        separatorPanel2.setBackground(new java.awt.Color(204, 204, 204));
        renameOption = new customMenuItem("<html><b>Đổi tên</b></html>", new ImageIcon(getClass().getResource("/view/img/edit.png")));
        moveOption = new customMenuItem("<html><b>Di chuyển</b></html>", new ImageIcon(getClass().getResource("/view/img/move.png")));
        copyOption = new customMenuItem("<html><b>Sao chép</b></html>", new ImageIcon(getClass().getResource("/view/img/copy.png")));
        deleteOption = new customMenuItem("<html><b>Chuyển vào thùng rác</b></html>", new ImageIcon(getClass().getResource("/view/img/delete.png")));
        downloadOption = new customMenuItem("<html><b>Tải xuống</b></html>", new ImageIcon(getClass().getResource("/view/img/downloads.png")));
        shareOption = new customMenuItem("<html><b>Chia sẻ</b></html>", new ImageIcon(getClass().getResource("/view/img/share.png")));
        optionMenu.add(renameOption);
        optionMenu.add(separatorPanel);
        optionMenu.add(moveOption);
        optionMenu.add(copyOption);
        optionMenu.add(deleteOption);
        optionMenu.add(separatorPanel2);
        optionMenu.add(downloadOption);
        optionMenu.add(shareOption);

    }

    public void initEvent(TableActionEvent event, int row) {
        renameOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.onRename(row);
            }
        });
        moveOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.onMove(row);
            }
        });
        copyOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.onCopy(row);
            }
        });
        deleteOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.onDelete(row);
            }
        });
        downloadOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.onDownload(row);
            }
        });
        shareOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                event.onShare(row);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        optionMenu = new view.custom.CustomPopupMenu();
        editBtn = new view.custom.ActionButton();

        optionMenu.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 216, 216)));
        optionMenu.setBackgroundColor(java.awt.Color.white);

        editBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/img/dots.png"))); // NOI18N
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(103, Short.MAX_VALUE)
                .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(editBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        optionMenu.show(editBtn, editBtn.getX() - 200, editBtn.getY());
    }//GEN-LAST:event_editBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private view.custom.ActionButton editBtn;
    private view.custom.CustomPopupMenu optionMenu;
    // End of variables declaration//GEN-END:variables
}
