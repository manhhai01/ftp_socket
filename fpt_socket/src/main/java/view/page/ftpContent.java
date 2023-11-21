/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.page;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URLDecoder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.*;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import payloads.DataResponse;
import payloads.UserPermission;
import socket.StatusCode;
import socket.socketManager;
import view.custom.IconRenderer;
import view.custom.TableActionCellEditor;
import view.custom.TableActionCellRender;
import view.custom.TableActionEvent;
import view.custom.customDialog;
import view.custom.folderPermission;

/**
 *
 * @author Bum
 */ 
public final class ftpContent extends javax.swing.JPanel {

    private Stack<String> pathHistory = new Stack<>();
    private final String CONTENT_TYPE;
    private final String SHARE_CONTENT="share",MYSPACE_CONTENT="myWorkingSpace";
    private final String ROOT_DIRECTORY;
    private String CURRENT_DIRECTORY;
    private String oldName,moveFileName,shareFileName;

    /**
     * Creates new form page1
     * @param type
     * @param rootDir
     */
    public ftpContent(String type, String rootDir) throws Exception {
        initComponents();
        this.CONTENT_TYPE = type;
        setTable();
        ROOT_DIRECTORY = rootDir;
        
        createCustomdialog();
        createPasteOption();
        if(CONTENT_TYPE.equals(MYSPACE_CONTENT)){
            pathHistory.push(rootDir);
            if(socketManager.getInstance().changeDirectory(ROOT_DIRECTORY).getStatus()==StatusCode.FILE_ACTION_OK){
                changePathTitle();
                getFileList();
            }
        }else {
            changePathTitle();
            getSharedFileList();
        }        
    }
    
    public void setTable(){
        table.setTableHeader(null);               
        TableActionEvent event = new TableActionEvent() {
            @Override
            public void onRename(int row) {
                renameTitle.setText("Đổi tên");
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                // lưu lại tên cũ 
                oldName = model.getValueAt(row,1 ).toString();
                renameField.setText(oldName);
                renameForm.setVisible(true);
            }

            @Override
            public void onDelete(int row) {
                if (table.isEditing()) {
                    table.getCellEditor().stopCellEditing();
                }
                String name = getFilePath(row);
                try{
                    DataResponse res = socketManager.getInstance().delete(name);
                    if(res.getStatus()== StatusCode.FILE_ACTION_OK){
                        JOptionPane.showMessageDialog(parentFrame, "đã xóa!", "Success", INFORMATION_MESSAGE);
                        getFileList();
                    }
                }catch (Exception e){
                    Logger.getLogger(ftpContent.class.getName()).log(Level.SEVERE, null, e);
     
                }
            }


            @Override
            public void onMove(int row) {
                try {
                    moveFileName=getFilePath(row);
                    if(socketManager.getInstance().checkPermissionForMoveCommand(moveFileName).getStatus()==StatusCode.FILE_ACTION_NOT_TAKEN){
                        moveFileName = null;
                        JOptionPane.showMessageDialog(parentFrame,"Bạn ko có quyền chỉnh sửa tệp này!", "Thông báo",WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ftpContent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void onDownload(int row) {
                System.out.println("Download row : " + row); ; 
            }

            @Override
            public void onShare(int row) {
                try{
                    shareFileName=getFilePath(row);
                    String type = shareFileName.split("\\.").length<1?"dir":"file";
                    List<UserPermission> userList = socketManager.getInstance().getShareUserList(shareFileName);
                    shareUserContent.removeAll();
                    shareUserContent.revalidate();
                    shareUserContent.repaint();
                    shareUserContent.setLayout(new BoxLayout(shareUserContent,BoxLayout.Y_AXIS));
                    if(type.equals("dir"))
                        for(UserPermission user : userList){
                            folderPermission panel = new folderPermission(user,shareFileName);
                            panel.setPreferredSize(new Dimension(500,100));
                            shareUserContent.add(panel);
                        }
                    shareForm.setVisible(true);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        table.getColumnModel().getColumn(0).setCellRenderer(new IconRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(6).setCellEditor(new TableActionCellEditor(event));
        table.setDefaultRenderer(Object.class, (JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
            Component component = new DefaultTableCellRenderer().getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
            if (isSelected == false ) {
            component.setBackground(Color.WHITE);
            }
            if(column==3){
                ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
            }
            return component;
        });
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Object[] row = new Object[]{new ImageIcon(getClass().getResource("/view/img/cloud-upload.png")),"Row 2","","",""};
        model.addRow(row);
    }
    public void createCustomdialog(){
        parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        panel = this;
        renameForm = new customDialog(parentFrame);
        renameForm.setDialogContent(renamePanel);
        shareForm = new customDialog(parentFrame);
        shareForm.setDialogContent(sharePanel);
    }
    public void closeDialog(){
        renameForm.setVisible(false);
        renameField.setText("");
    }
    public void createPasteOption(){
        pasteOption = new JPopupMenu();
        menuItem = new JMenuItem("Dán");
        pasteOption.add(menuItem);
        menuItem.addActionListener((ActionEvent e) -> {
            if (isRootShare()) {
                JOptionPane.showMessageDialog(parentFrame,"Bạn không thể di chuyển thư mục đến đây!", "Thông báo",WARNING_MESSAGE);
                return;
            }
            String pathTo = pathHistory.peek();
            int x1 = moveFileName.lastIndexOf("/");
            String pathFrom = moveFileName.substring(0, x1);
            if(pathFrom.equals(pathTo)){
                JOptionPane.showMessageDialog(parentFrame,"Tệp đã nằm ở thư mục này rồi, yêu cầu bị từ chối !", "Thông báo",WARNING_MESSAGE);
                return;
            }
            String name1 = moveFileName.substring(x1 + 1);
            pathTo = pathTo+"/" + name1;
            try {
                System.out.println("From: "+moveFileName);
                System.out.println("To: "+pathTo);
                DataResponse res = socketManager.getInstance().move(pathTo);
                if(res.getStatus() == StatusCode.FILE_ACTION_OK){
                    JOptionPane.showMessageDialog(parentFrame,"Di chuyển thành công!", "Thông báo",INFORMATION_MESSAGE);
                    getFileList();
                }else JOptionPane.showMessageDialog(parentFrame,"bạn không đủ quyền hạn để di chuyển tệp đến thư mục này!", "Thông báo",WARNING_MESSAGE);
            } catch (Exception ex) {
                Logger.getLogger(ftpContent.class.getName()).log(Level.SEVERE, null, ex);
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

        renamePanel = new javax.swing.JPanel();
        renameConfirm = new view.custom.Button();
        renameCancel = new view.custom.Button();
        renameTitle = new javax.swing.JLabel();
        renameField = new view.custom.textField();
        sharePanel = new javax.swing.JPanel();
        textField2 = new view.custom.textField();
        renameConfirm2 = new view.custom.Button();
        roundPanel2 = new view.custom.RoundPanel();
        access5 = new javax.swing.JScrollPane();
        shareUserContent = new javax.swing.JPanel();
        renameConfirm3 = new view.custom.Button();
        roundPanel1 = new view.custom.RoundPanel();
        jSeparator1 = new javax.swing.JSeparator();
        title = new javax.swing.JLabel();
        roundPanel3 = new view.custom.RoundPanel();
        searchField = new javax.swing.JTextField();
        searchBtn = new view.custom.imageIcon();
        highlightPanel1 = new view.custom.HighlightPanel();
        roundPanel4 = new view.custom.RoundPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        highlightPanel5 = new view.custom.HighlightPanel();
        jLabel8 = new javax.swing.JLabel();
        imageIcon4 = new view.custom.imageIcon();
        highlightPanel6 = new view.custom.HighlightPanel();
        jLabel9 = new javax.swing.JLabel();
        imageIcon5 = new view.custom.imageIcon();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        highlightPanel3 = new view.custom.HighlightPanel();
        jLabel12 = new javax.swing.JLabel();

        renamePanel.setBackground(new java.awt.Color(255, 255, 255));
        renamePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 216, 216)));

        renameConfirm.setText("Xác nhận");
        renameConfirm.setColor(new java.awt.Color(204, 204, 255));
        renameConfirm.setColorClick(new java.awt.Color(153, 153, 153));
        renameConfirm.setColorOver(new java.awt.Color(102, 102, 102));
        renameConfirm.setRadius(10);
        renameConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameConfirmActionPerformed(evt);
            }
        });

        renameCancel.setText("Hủy");
        renameCancel.setColor(new java.awt.Color(204, 204, 255));
        renameCancel.setColorClick(new java.awt.Color(153, 153, 153));
        renameCancel.setColorOver(new java.awt.Color(102, 102, 102));
        renameCancel.setRadius(10);
        renameCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameCancelActionPerformed(evt);
            }
        });

        renameTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        renameTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        renameTitle.setText("Nhập tên");

        renameField.setLabelText("Nhập tên");

        javax.swing.GroupLayout renamePanelLayout = new javax.swing.GroupLayout(renamePanel);
        renamePanel.setLayout(renamePanelLayout);
        renamePanelLayout.setHorizontalGroup(
            renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renamePanelLayout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(renameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(renamePanelLayout.createSequentialGroup()
                        .addComponent(renameConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(renameCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(renameField, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(119, Short.MAX_VALUE))
        );
        renamePanelLayout.setVerticalGroup(
            renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renamePanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(renameTitle)
                .addGap(18, 18, 18)
                .addComponent(renameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(renameCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(renameConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        sharePanel.setBackground(new java.awt.Color(255, 255, 255));
        sharePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        textField2.setLabelText("Nhập tên user");

        renameConfirm2.setText("Thêm");
        renameConfirm2.setColor(new java.awt.Color(204, 204, 255));
        renameConfirm2.setColorClick(new java.awt.Color(153, 153, 153));
        renameConfirm2.setColorOver(new java.awt.Color(102, 102, 102));
        renameConfirm2.setRadius(10);
        renameConfirm2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameConfirm2ActionPerformed(evt);
            }
        });

        access5.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout shareUserContentLayout = new javax.swing.GroupLayout(shareUserContent);
        shareUserContent.setLayout(shareUserContentLayout);
        shareUserContentLayout.setHorizontalGroup(
            shareUserContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 531, Short.MAX_VALUE)
        );
        shareUserContentLayout.setVerticalGroup(
            shareUserContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 262, Short.MAX_VALUE)
        );

        access5.setViewportView(shareUserContent);

        javax.swing.GroupLayout roundPanel2Layout = new javax.swing.GroupLayout(roundPanel2);
        roundPanel2.setLayout(roundPanel2Layout);
        roundPanel2Layout.setHorizontalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(access5, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE))
        );
        roundPanel2Layout.setVerticalGroup(
            roundPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(access5, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        renameConfirm3.setBackground(new java.awt.Color(255, 255, 255));
        renameConfirm3.setText("X");
        renameConfirm3.setColor(new java.awt.Color(255, 255, 255));
        renameConfirm3.setColorClick(new java.awt.Color(153, 153, 153));
        renameConfirm3.setColorOver(new java.awt.Color(102, 102, 102));
        renameConfirm3.setRadius(10);
        renameConfirm3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameConfirm3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sharePanelLayout = new javax.swing.GroupLayout(sharePanel);
        sharePanel.setLayout(sharePanelLayout);
        sharePanelLayout.setHorizontalGroup(
            sharePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sharePanelLayout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addGroup(sharePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sharePanelLayout.createSequentialGroup()
                        .addGroup(sharePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(roundPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(sharePanelLayout.createSequentialGroup()
                                .addComponent(textField2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(renameConfirm2, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(50, 50, 50))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sharePanelLayout.createSequentialGroup()
                        .addComponent(renameConfirm3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        sharePanelLayout.setVerticalGroup(
            sharePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sharePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(renameConfirm3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sharePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(renameConfirm2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(roundPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        setPreferredSize(new java.awt.Dimension(1066, 666));
        setVerifyInputWhenFocusTarget(false);

        roundPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jSeparator1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        title.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        title.setText("/");

        roundPanel3.setBackground(new java.awt.Color(204, 204, 204));
        roundPanel3.setPreferredSize(new java.awt.Dimension(300, 40));
        roundPanel3.setRadius(40);

        searchField.setBackground(new java.awt.Color(204, 204, 204));
        searchField.setToolTipText("Nhập tên tệp cần tìm");
        searchField.setBorder(null);
        searchField.setInheritsPopupMenu(true);
        searchField.setName("Nhập tên tệp cần tìm"); // NOI18N
        searchField.setPreferredSize(new java.awt.Dimension(64, 25));
        searchField.setRequestFocusEnabled(false);
        searchField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchFieldMouseClicked(evt);
            }
        });
        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        searchBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/img/magnifying-glass.png"))); // NOI18N
        searchBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                searchBtnMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout roundPanel3Layout = new javax.swing.GroupLayout(roundPanel3);
        roundPanel3.setLayout(roundPanel3Layout);
        roundPanel3Layout.setHorizontalGroup(
            roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(searchBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        roundPanel3Layout.setVerticalGroup(
            roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel3Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addGroup(roundPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(searchField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        highlightPanel1.setColor(java.awt.Color.white);
        highlightPanel1.setColorClick(new java.awt.Color(153, 153, 153));
        highlightPanel1.setColorOver(new java.awt.Color(204, 204, 204));
        highlightPanel1.setPreferredSize(new java.awt.Dimension(171, 40));
        highlightPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                highlightPanel1MouseClicked(evt);
            }
        });

        roundPanel4.setBackground(new java.awt.Color(51, 204, 0));
        roundPanel4.setRadius(24);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("+");

        javax.swing.GroupLayout roundPanel4Layout = new javax.swing.GroupLayout(roundPanel4);
        roundPanel4.setLayout(roundPanel4Layout);
        roundPanel4Layout.setHorizontalGroup(
            roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
        );
        roundPanel4Layout.setVerticalGroup(
            roundPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Tạo thư mục mới");

        javax.swing.GroupLayout highlightPanel1Layout = new javax.swing.GroupLayout(highlightPanel1);
        highlightPanel1.setLayout(highlightPanel1Layout);
        highlightPanel1Layout.setHorizontalGroup(
            highlightPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, highlightPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(roundPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        highlightPanel1Layout.setVerticalGroup(
            highlightPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(highlightPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(highlightPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(roundPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        highlightPanel5.setBackground(new java.awt.Color(255, 255, 255));
        highlightPanel5.setColor(java.awt.Color.white);
        highlightPanel5.setColorClick(new java.awt.Color(153, 153, 153));
        highlightPanel5.setColorOver(new java.awt.Color(204, 204, 204));
        highlightPanel5.setPreferredSize(new java.awt.Dimension(171, 40));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("upload thư mục");

        imageIcon4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/img/cloud-upload.png"))); // NOI18N

        javax.swing.GroupLayout highlightPanel5Layout = new javax.swing.GroupLayout(highlightPanel5);
        highlightPanel5.setLayout(highlightPanel5Layout);
        highlightPanel5Layout.setHorizontalGroup(
            highlightPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, highlightPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageIcon4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        highlightPanel5Layout.setVerticalGroup(
            highlightPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(highlightPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(highlightPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(highlightPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(imageIcon4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        highlightPanel6.setBackground(new java.awt.Color(255, 255, 255));
        highlightPanel6.setColor(java.awt.Color.white);
        highlightPanel6.setColorClick(new java.awt.Color(153, 153, 153));
        highlightPanel6.setColorOver(new java.awt.Color(204, 204, 204));
        highlightPanel6.setPreferredSize(new java.awt.Dimension(171, 40));
        highlightPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                highlightPanel6MouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("upload tệp");

        imageIcon5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view/img/cloud-upload.png"))); // NOI18N

        javax.swing.GroupLayout highlightPanel6Layout = new javax.swing.GroupLayout(highlightPanel6);
        highlightPanel6.setLayout(highlightPanel6Layout);
        highlightPanel6Layout.setHorizontalGroup(
            highlightPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, highlightPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(imageIcon5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        highlightPanel6Layout.setVerticalGroup(
            highlightPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(highlightPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(highlightPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(highlightPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(imageIcon5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jScrollPane1.setBorder(null);
        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jScrollPane1MousePressed(evt);
            }
        });

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "Example1", "Chinh", "30 thg 9, 2023", "69 bytes", null, null},
                {null, "Example2", "Chinh", "30 thg 9, 2023", "69 bytes", null, null},
                {null, "Example3", "Chinh", "30 thg 9, 2023", "69 bytes", null, null},
                {null, "Example4", "Chinh", "30 thg 9, 2023", "69 bytes", null, null}
            },
            new String [] {
                "", "Tên", "Chủ sở hữu", "Ngày sửa đổi", "Kích thước tệp", "", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setFillsViewportHeight(true);
        table.setFocusable(false);
        table.setGridColor(new java.awt.Color(216, 216, 216));
        table.setRowHeight(60);
        table.setSelectionBackground(new java.awt.Color(230, 230, 230));
        table.setSelectionForeground(new java.awt.Color(51, 51, 51));
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tableMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMinWidth(40);
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(1).setPreferredWidth(250);
            table.getColumnModel().getColumn(2).setPreferredWidth(50);
            table.getColumnModel().getColumn(3).setPreferredWidth(150);
            table.getColumnModel().getColumn(4).setPreferredWidth(50);
            table.getColumnModel().getColumn(5).setMinWidth(0);
            table.getColumnModel().getColumn(5).setPreferredWidth(0);
            table.getColumnModel().getColumn(5).setMaxWidth(0);
            table.getColumnModel().getColumn(6).setPreferredWidth(10);
        }

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Tên");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Chủ sở hữu");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Ngày sửa đổi");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Kích cỡ tệp");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(336, 336, 336)
                        .addComponent(jLabel5)
                        .addGap(174, 174, 174)
                        .addComponent(jLabel6)
                        .addGap(76, 76, 76)
                        .addComponent(jLabel7)
                        .addContainerGap(179, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(3, 3, 3))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        highlightPanel3.setColor(java.awt.Color.white);
        highlightPanel3.setColorClick(new java.awt.Color(153, 153, 153));
        highlightPanel3.setColorOver(new java.awt.Color(204, 204, 204));
        highlightPanel3.setPreferredSize(new java.awt.Dimension(171, 40));
        highlightPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                highlightPanel3MouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Segoe UI", 2, 14)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("< quay lại");

        javax.swing.GroupLayout highlightPanel3Layout = new javax.swing.GroupLayout(highlightPanel3);
        highlightPanel3.setLayout(highlightPanel3Layout);
        highlightPanel3Layout.setHorizontalGroup(
            highlightPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(highlightPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        highlightPanel3Layout.setVerticalGroup(
            highlightPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(highlightPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout roundPanel1Layout = new javax.swing.GroupLayout(roundPanel1);
        roundPanel1.setLayout(roundPanel1Layout);
        roundPanel1Layout.setHorizontalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addContainerGap(19, Short.MAX_VALUE)
                        .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 1041, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(roundPanel1Layout.createSequentialGroup()
                        .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundPanel1Layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(roundPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(71, 71, 71)
                                .addComponent(highlightPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(highlightPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(highlightPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(roundPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1031, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(roundPanel1Layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(highlightPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        roundPanel1Layout.setVerticalGroup(
            roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(roundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roundPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(highlightPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(highlightPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(highlightPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(highlightPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseClicked
        // sự kiện double click lên 1 row
        if (evt.getClickCount() == 2) {
            JTable target = (JTable) evt.getSource();
            int row = target.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            String name;
            if(isRootShare())
                name= model.getValueAt(row,5).toString();
            else name= model.getValueAt(row,1).toString();
            if(name.split("\\.").length==1){
                // nếu row được chọn là thư mục
                try {
                    if(socketManager.getInstance().changeDirectory(name).getStatus() == StatusCode.FILE_ACTION_OK); 
                    {
                        String newPath=null;
                        if(pathHistory.size() <= 1 && this.CONTENT_TYPE.equals(SHARE_CONTENT))
                            newPath= name;
                        else newPath=pathHistory.isEmpty() ? ROOT_DIRECTORY : pathHistory.peek() + "/" + name;
                        pathHistory.push(newPath);
                        changePathTitle();
                        getFileList();
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ftpContent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                // nếu row được chọn là file
            }
        }
    }//GEN-LAST:event_tableMouseClicked

    private void searchBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchBtnMouseClicked
        //Tìm kiếm
        String input=searchField.getText();
    }//GEN-LAST:event_searchBtnMouseClicked

    private void renameCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameCancelActionPerformed
        closeDialog();
    }//GEN-LAST:event_renameCancelActionPerformed

    private void highlightPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_highlightPanel1MouseClicked
        renameTitle.setText("Tạo thư mục");
        renameForm.setVisible(true);

    }//GEN-LAST:event_highlightPanel1MouseClicked

    private void renameConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameConfirmActionPerformed
        if(renameTitle.getText().equals("Tạo thư mục")){
            //Tạo thư mục
            if(!renameField.getText().isEmpty()){
            String name = renameField.getText();
                DataResponse response;
                try {
                    response = socketManager.getInstance().createNewFolder(name);
                    if(response.getStatus()== StatusCode.DIRECTORY_CREATED){
                        getFileList();
                    }else JOptionPane.showMessageDialog(parentFrame, response.getMessage());
                } catch (Exception ex) {
                    Logger.getLogger(ftpContent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        
            
        }else {
            //Đổi tên file
            if(!renameField.getText().isEmpty()){
                try {
                    String newName = renameField.getText();
                    DataResponse res = socketManager.getInstance().rename(oldName, newName);
                    if(res.getStatus()==StatusCode.FILE_ACTION_OK){
                        getFileList();
                    }else {
                        JOptionPane.showMessageDialog(parentFrame, res.getMessage(), "Thông báo",WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ftpContent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        closeDialog();
    }//GEN-LAST:event_renameConfirmActionPerformed

    private void highlightPanel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_highlightPanel3MouseClicked
        try {
            String newPath = null;
            if(isRootShare()){
                getSharedFileList();
                changePathTitle();
                
            }else{
                if(pathHistory.size()>=1){
                        pathHistory.pop();
                        newPath = pathHistory.peek();
                }else if(!this.CONTENT_TYPE.equals(MYSPACE_CONTENT))
                    newPath = ROOT_DIRECTORY;

                if(socketManager.getInstance().changeDirectory(newPath).getStatus() == StatusCode.FILE_ACTION_OK){
                    changePathTitle();
                    getFileList();                
                }

            }
        } catch (Exception ex) {
                Logger.getLogger(ftpContent.class.getName()).log(Level.SEVERE, null, ex);
            }
    }//GEN-LAST:event_highlightPanel3MouseClicked

    private void jScrollPane1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MousePressed

    }//GEN-LAST:event_jScrollPane1MousePressed

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed

    }//GEN-LAST:event_jPanel1MousePressed

    private void tableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMousePressed
                if (SwingUtilities.isRightMouseButton(evt)) {
                    if(moveFileName!=null) 
                    pasteOption.show(table,evt.getX(), evt.getY());
                    
                }
    }//GEN-LAST:event_tableMousePressed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        
    }//GEN-LAST:event_searchFieldActionPerformed

    private void searchFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_searchFieldMouseClicked
        searchField.requestFocus();
    }//GEN-LAST:event_searchFieldMouseClicked

    private void highlightPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_highlightPanel6MouseClicked
        System.out.println(pathHistory.peek());
    }//GEN-LAST:event_highlightPanel6MouseClicked

    private void renameConfirm2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameConfirm2ActionPerformed

    }//GEN-LAST:event_renameConfirm2ActionPerformed

    private void renameConfirm3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameConfirm3ActionPerformed
        shareForm.setVisible(false);
    }//GEN-LAST:event_renameConfirm3ActionPerformed
    public boolean isRootShare(){
        return pathHistory.size()<1 && CONTENT_TYPE.equals(SHARE_CONTENT);
    }
    
    public void changePathTitle(){
        if(isRootShare())
            title.setText("/");
        else title.setText(pathHistory.peek());
    }
    public boolean getCurrentDir(){
        DataResponse response;
        try {
            response = socketManager.getInstance().getCurrentWorkingDirectory();
            if(response.getStatus() == StatusCode.CURRENT_WORKING_DIRECTORY){
                String dir = response.getMessage().substring(0,response.getMessage().lastIndexOf("\""));       
                CURRENT_DIRECTORY = dir.replace("\"", "");
                title.setText(CURRENT_DIRECTORY);
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(ftpContent.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        JOptionPane.showMessageDialog(parentFrame, "Lỗi xảy ra khi tìm thư mục hiện thành", "Thông báo",WARNING_MESSAGE);
        return false;       
    }
    public String getFilePath(int row){
        String filepath;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if(isRootShare())
            filepath = model.getValueAt(row,5).toString();
        else filepath = pathHistory.peek()+"/"+model.getValueAt(row,1 ).toString();
        return filepath;
    }
    
    
    
    public void getFileList() throws Exception{
        String fileList = socketManager.getInstance().getFileList(pathHistory.peek());
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        if(!fileList.isBlank()){
            String[] lines = fileList.split("\n");
            for(String line : lines){
                if(!line.isBlank()){
                String[] parts = line.split(";");
                String type = parts[0].split("=")[1].trim();
                String owner =parts[1].split("=")[1].trim();
                String modify=parts[2].split("=")[1].trim();
                String size = parts[3].split("=")[1].trim();
                String perm = parts[4].split("=")[1].trim();
                String name = URLDecoder.decode(parts[5],"UTF-8").trim();
                modify = convertTimestamp(modify);
                ImageIcon img;
                try {
                    if (type.equals("dir")) {
                        img = new ImageIcon(getClass().getResource("/view/img/fileIcon/folder.png"));
                    } else {
                        img = new ImageIcon(getClass().getResource("/view/img/fileIcon/" + name.split("\\.")[1] + ".png"));
                    }
                } catch (Exception ex) {
                    img = new ImageIcon(getClass().getResource("/view/img/fileIcon/txt.png"));
                }
                Object[] row = new Object[]{img,name,owner.equals("null")?"Tôi":owner,modify,convertBytes(size)};
                // chia đơn vị mb,kg,Gb ******
                model.addRow(row);
                }
            }
        }
    }
    public void getSharedFileList() throws Exception{
        String fileList = socketManager.getInstance().getSharedFiles();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        if(!fileList.isBlank()){
            String[] lines = fileList.split("\n");
            for(String line : lines){
                if(!line.isBlank()){
                String[] parts = line.split(";");
                String type = parts[0].split("=")[1].trim();
                String owner =parts[1].split("=")[1].trim();
                String modify=parts[2].split("=")[1].trim();
                String size = parts[3].split("=")[1].trim();
                String perm = parts[4].split("=")[1].trim();
                parts = parts[5].trim().split(" ");
                String name = URLDecoder.decode(parts[0],"UTF-8").trim();
                String path = URLDecoder.decode(parts[1],"UTF-8").trim();
                modify = convertTimestamp(modify);
                ImageIcon img;
                try {
                    if (type.equals("dir")) {
                        img = new ImageIcon(getClass().getResource("/view/img/fileIcon/folder.png"));
                    } else {
                        img = new ImageIcon(getClass().getResource("/view/img/fileIcon/" + name.split("\\.")[1] + ".png"));
                    }
                } catch (Exception ex) {
                    img = new ImageIcon(getClass().getResource("/view/img/fileIcon/txt.png"));
                }
                Object[] row;
                row=new Object[]{img,name,owner,modify,convertBytes(size),path};
                model.addRow(row);
                }
            }
        }
    }
           
              

    
    
    public String convertTimestamp(String timestamp){
        long time=Long.parseLong(timestamp);
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        boolean isToday= dateTime.toLocalDate().isEqual(LocalDateTime.now().toLocalDate());
        String pattern = isToday ? "HH:mm":"dd 'th 'MM, yyyy";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern,new Locale("vi"));
        return dateTime.format(formatter);
    }
    public String convertBytes(String bytesToString) {
        long bytes = Long.parseLong(bytesToString);
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", (double) bytes / 1024);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", (double) bytes / (1024 * 1024));
        } else {
            return String.format("%.2f GB", (double) bytes / (1024 * 1024 * 1024));
        }
    }
    
    
    
    private Frame parentFrame;
    private JPanel panel;
    private JMenuItem menuItem;
    private customDialog renameForm,shareForm;
    private JPopupMenu pasteOption;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane access5;
    private view.custom.HighlightPanel highlightPanel1;
    private view.custom.HighlightPanel highlightPanel3;
    private view.custom.HighlightPanel highlightPanel5;
    private view.custom.HighlightPanel highlightPanel6;
    private view.custom.imageIcon imageIcon4;
    private view.custom.imageIcon imageIcon5;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private view.custom.Button renameCancel;
    private view.custom.Button renameConfirm;
    private view.custom.Button renameConfirm2;
    private view.custom.Button renameConfirm3;
    private view.custom.textField renameField;
    private javax.swing.JPanel renamePanel;
    private javax.swing.JLabel renameTitle;
    private view.custom.RoundPanel roundPanel1;
    private view.custom.RoundPanel roundPanel2;
    private view.custom.RoundPanel roundPanel3;
    private view.custom.RoundPanel roundPanel4;
    private view.custom.imageIcon searchBtn;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel sharePanel;
    private javax.swing.JPanel shareUserContent;
    private javax.swing.JTable table;
    private view.custom.textField textField2;
    private javax.swing.JLabel title;
    // End of variables declaration//GEN-END:variables
}
