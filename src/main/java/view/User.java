/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import bus.UserBus;
import com.beust.ah.A;
import java.awt.Component;
import java.util.Date;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import payload.response.UserDetailResponse;
import payload.response.UserResponse;
import utils.FormatUtils;

/**
 *
 * @author Son
 */
public class User extends javax.swing.JPanel {
    private UserBus userBus;
    private UserDetailResponse userInfo;
    /**
     * Creates new form User
     */
    public User() {
        initComponents();
        userBus = new UserBus();
        setTable();
        getAllUser();
    }
    
    
    
    
    
    
    
    
    public void setTable(){
            userTable.setTableHeader(null);    
            userTable.setDefaultRenderer(Object.class, (JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) -> {
            Component component = new DefaultTableCellRenderer().getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
            if (isSelected == false ) {
            component.setBackground(new java.awt.Color(255,255,255));
            }
            if(column==3){
                ((JLabel) component).setHorizontalAlignment(JLabel.CENTER);
            }
            return component;
        });
    }
    
    public void getAllUser(){
        List<UserResponse> userList = userBus.getAllUsers();
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);
        int i = 0;
        for(UserResponse user : userList){
            Object[] row = new Object[]{++i,user.getUsername(),user.getCreate_date(),(user.getIsActive()==1?"kích hoạt":"chưa kích hoạt")};
            model.addRow(row);
        }
    }
    public void getUserInfo(UserDetailResponse userInfo){  
        this.userInfo = userInfo;
        long usedBytes=userInfo.getUsedBytes();
        String email = userInfo.getUsername();
        String firstname = userInfo.getFirstName();
        String lastname = userInfo.getLastName();
        String gender = userInfo.getGender();
        Date birthdate = userInfo.getBirthdate();
        boolean anonymous = userInfo.isAnonymous();
        boolean isBlockDownload = userInfo.isBlockDownload();
        boolean isBlockUpload = userInfo.isBlockUpload();
        String quotaInBytesToString=FormatUtils.convertBytes(userInfo.getQuotaInBytes());
        String maxUploadSizeBytesToString= FormatUtils.convertBytes(userInfo.getMaxUploadFileSizeBytes());
        String maxDownloadSizeBytesToString = FormatUtils.convertBytes(userInfo.getMaxDownloadFileSizeBytes());
        String[] quotaInBytesPart = quotaInBytesToString.split(" ");
        String[] maxUploadSizeBytesParts = maxUploadSizeBytesToString.split(" ");
        String[] maxDownloadSizeBytesParts = maxDownloadSizeBytesToString.split(" ");
        quotaUnit.setSelectedItem(quotaInBytesPart[1]);
        uploadUnit.setSelectedItem(maxUploadSizeBytesParts[1]);
        downloadUnit.setSelectedItem(maxDownloadSizeBytesParts[1]);
        firstnameField.setText(firstname!=null?firstname:"");
        lastnameField.setText(lastname!=null?lastname:"");
        birthdateField.setDate(birthdate!=null?birthdate:new Date());
        emailField.setText(email);
        if(gender!=null)
            if(gender.equals("Nam"))
                male.setSelected(true);
            else female.setSelected(true);
        anonymousRb.setSelected(anonymous);
        uploadRb.setSelected(!isBlockUpload);
        downloadRb.setSelected(!isBlockDownload);
        maxuploadField.setText(maxUploadSizeBytesParts[0]);
        maxdownloadField.setText(maxDownloadSizeBytesParts[0]);
        usedField.setText(FormatUtils.convertBytes(usedBytes));
        quotaField.setText(quotaInBytesPart[0]);
        
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
        refreshBtn = new view.custom.Button();
        renameTitle = new javax.swing.JLabel();
        lastnameField = new view.custom.textField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        firstnameField = new view.custom.textField();
        jLabel12 = new javax.swing.JLabel();
        birthdateField = new de.wannawork.jcalendar.JCalendarComboBox();
        jLabel25 = new javax.swing.JLabel();
        male = new javax.swing.JRadioButton();
        female = new javax.swing.JRadioButton();
        jLabel26 = new javax.swing.JLabel();
        emailField = new view.custom.textField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel27 = new javax.swing.JLabel();
        usedField = new view.custom.textField();
        jLabel28 = new javax.swing.JLabel();
        quotaField = new view.custom.textField();
        jLabel29 = new javax.swing.JLabel();
        maxuploadField = new view.custom.textField();
        jLabel30 = new javax.swing.JLabel();
        maxdownloadField = new view.custom.textField();
        jLabel31 = new javax.swing.JLabel();
        downloadRb = new javax.swing.JRadioButton();
        uploadRb = new javax.swing.JRadioButton();
        anonymousRb = new javax.swing.JRadioButton();
        uploadUnit = new javax.swing.JComboBox<>();
        downloadUnit = new javax.swing.JComboBox<>();
        quotaUnit = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        renamePanel.setBackground(new java.awt.Color(255, 255, 255));
        renamePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 216, 216)));

        renameConfirm.setText("Lưu");
        renameConfirm.setColor(new java.awt.Color(204, 204, 255));
        renameConfirm.setColorClick(new java.awt.Color(153, 153, 153));
        renameConfirm.setColorOver(new java.awt.Color(102, 102, 102));
        renameConfirm.setRadius(10);
        renameConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameConfirmActionPerformed(evt);
            }
        });

        refreshBtn.setText("Refresh");
        refreshBtn.setColor(new java.awt.Color(204, 204, 255));
        refreshBtn.setColorClick(new java.awt.Color(153, 153, 153));
        refreshBtn.setColorOver(new java.awt.Color(102, 102, 102));
        refreshBtn.setRadius(10);
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        renameTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        renameTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        renameTitle.setText("Thông tin cá nhân");

        lastnameField.setFocusable(false);
        lastnameField.setLabelText("");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Họ:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Tên:");

        firstnameField.setFocusable(false);
        firstnameField.setLabelText("");

        jLabel12.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel12.setText("Ngày sinh:");

        birthdateField.setEnabled(false);
        birthdateField.setFocusable(false);
        birthdateField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel25.setText("Giới tính:");

        male.setText("Nam");
        male.setEnabled(false);
        male.setFocusPainted(false);
        male.setFocusable(false);
        male.setRequestFocusEnabled(false);

        female.setText("Nữ");
        female.setEnabled(false);
        female.setFocusPainted(false);
        female.setFocusable(false);
        female.setRolloverEnabled(false);

        jLabel26.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel26.setText("Email:");

        emailField.setFocusable(false);
        emailField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        emailField.setLabelText("");

        jLabel27.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel27.setText("Bộ nhớ sử dụng:");

        usedField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        usedField.setFocusable(false);
        usedField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        usedField.setLabelText("");
        usedField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usedFieldActionPerformed(evt);
            }
        });

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel28.setText("/");

        quotaField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        quotaField.setLabelText("");
        quotaField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quotaFieldActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel29.setText("Dung lượng upload tối đa: ");

        maxuploadField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        maxuploadField.setLabelText("");
        maxuploadField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxuploadFieldActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel30.setText("Dung lượng download tối đa: ");

        maxdownloadField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        maxdownloadField.setLabelText("");
        maxdownloadField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxdownloadFieldActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel31.setText("Quyền:");

        downloadRb.setText("Download");
        downloadRb.setFocusable(false);

        uploadRb.setText("upload");
        uploadRb.setFocusable(false);

        anonymousRb.setText("anonymous");
        anonymousRb.setFocusable(false);

        uploadUnit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GB", "MB", "KB", "B" }));

        downloadUnit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GB", "MB", "KB", "B" }));

        quotaUnit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GB", "MB", "KB", "B" }));

        javax.swing.GroupLayout renamePanelLayout = new javax.swing.GroupLayout(renamePanel);
        renamePanel.setLayout(renamePanelLayout);
        renamePanelLayout.setHorizontalGroup(
            renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renamePanelLayout.createSequentialGroup()
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(renamePanelLayout.createSequentialGroup()
                        .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(renamePanelLayout.createSequentialGroup()
                                .addGap(94, 94, 94)
                                .addComponent(renameTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(renamePanelLayout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, renamePanelLayout.createSequentialGroup()
                                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, renamePanelLayout.createSequentialGroup()
                                            .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(renamePanelLayout.createSequentialGroup()
                                                    .addComponent(jLabel12)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(birthdateField, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE))
                                                .addGroup(renamePanelLayout.createSequentialGroup()
                                                    .addComponent(jLabel1)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(lastnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(jLabel2)))
                                            .addGap(18, 18, 18)
                                            .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(firstnameField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, renamePanelLayout.createSequentialGroup()
                                                    .addComponent(jLabel25)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(male)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(female))))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, renamePanelLayout.createSequentialGroup()
                                            .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(renamePanelLayout.createSequentialGroup()
                                                    .addComponent(jLabel31)
                                                    .addGap(28, 28, 28)
                                                    .addComponent(downloadRb)
                                                    .addGap(18, 18, 18)
                                                    .addComponent(uploadRb))
                                                .addComponent(renameConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(18, 18, 18)
                                            .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(anonymousRb)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, renamePanelLayout.createSequentialGroup()
                                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(usedField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(quotaField, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(quotaUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(renamePanelLayout.createSequentialGroup()
                                        .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(maxdownloadField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(renamePanelLayout.createSequentialGroup()
                                                .addComponent(maxuploadField, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                        .addGap(18, 18, 18)
                                        .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(uploadUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(downloadUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(20, 20, 20)))))
                        .addGap(26, 26, 26))
                    .addGroup(renamePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator1)))
                .addContainerGap())
        );
        renamePanelLayout.setVerticalGroup(
            renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(renamePanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(renameTitle)
                .addGap(18, 18, 18)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lastnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(firstnameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(birthdateField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(male)
                        .addComponent(female)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(usedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quotaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quotaUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxuploadField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(uploadUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxdownloadField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(downloadUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(downloadRb)
                    .addComponent(uploadRb)
                    .addComponent(anonymousRb))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(renamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(renameConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        jScrollPane4.setBorder(null);
        jScrollPane4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jScrollPane4MousePressed(evt);
            }
        });

        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, "Example1", "Chinh", "30 thg 9, 2023"},
                {null, "Example2", "Chinh", "30 thg 9, 2023"},
                {null, "Example3", "Chinh", "30 thg 9, 2023"},
                {null, "Example4", "Chinh", "30 thg 9, 2023"}
            },
            new String [] {
                "STT", "Tên", "Ngày khởi tạo", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTable.setFillsViewportHeight(true);
        userTable.setFocusable(false);
        userTable.setGridColor(new java.awt.Color(216, 216, 216));
        userTable.setRowHeight(60);
        userTable.setSelectionBackground(new java.awt.Color(204, 204, 204));
        userTable.setSelectionForeground(new java.awt.Color(51, 51, 51));
        userTable.setShowGrid(false);
        userTable.setShowHorizontalLines(true);
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                userTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                userTableMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(userTable);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("STT");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Tài khoản");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Trạng thái");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Ngày khởi tạo");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(151, 151, 151)
                        .addComponent(jLabel4)
                        .addGap(121, 121, 121)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(60, 60, 60))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 11, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Danh sách tài khoản");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(renamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(renamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void renameConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameConfirmActionPerformed
        String quotaInBytesToString = quotaField.getText();
        String quotaUnit = this.quotaUnit.getSelectedItem().toString();
        String maxUploadSizeBytesToString = maxuploadField.getText();
        String maxUploadUnit = uploadUnit.getSelectedItem().toString();
        String maxDownloadSizeBytesToString = maxuploadField.getText();
        String maxDownloadUnit = downloadUnit.getSelectedItem().toString();
        long quotaInBytes = getBytes(quotaInBytesToString,quotaUnit);
        long maxUpload = getBytes(maxUploadSizeBytesToString,maxUploadUnit);
        long maxDownload = getBytes(maxDownloadSizeBytesToString, maxDownloadUnit);
        userInfo.setQuotaInBytes(quotaInBytes);
        userInfo.setMaxDownloadFileSizeBytes(maxDownload);
        userInfo.setMaxUploadFileSizeBytes(maxUpload);
        userInfo.setAnonymous(anonymousRb.isSelected());
        userInfo.setBlockDownload(!downloadRb.isSelected());
        userInfo.setBlockUpload(!uploadRb.isSelected());
        if(userBus.saveUserDetail(userInfo))
            JOptionPane.showMessageDialog(renamePanel, "Lưu thành công", "Thông báo",INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(renamePanel, "Có lỗi xảy ra", "Thông báo", WARNING_MESSAGE);
       
    }//GEN-LAST:event_renameConfirmActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        String username = emailField.getText();
        UserDetailResponse userInfo = userBus.getUserByUsername(username);
        getUserInfo(userInfo);
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void usedFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usedFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_usedFieldActionPerformed

    private void quotaFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quotaFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quotaFieldActionPerformed

    private void maxuploadFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxuploadFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxuploadFieldActionPerformed

    private void maxdownloadFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxdownloadFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxdownloadFieldActionPerformed

    private void userTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTableMouseClicked
        if (evt.getClickCount() == 2) {
            JTable target = (JTable) evt.getSource();
            int row = target.getSelectedRow();
            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            String username = model.getValueAt(row, 1).toString();
            UserDetailResponse userInfo = userBus.getUserByUsername(username);
            getUserInfo(userInfo);
        }
    }//GEN-LAST:event_userTableMouseClicked

    private void userTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTableMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_userTableMousePressed

    private void jScrollPane4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane4MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jScrollPane4MousePressed
    public long getBytes(String bytesString,String unit){
        double bytes = Double.parseDouble(bytesString);
        switch(unit){
            case "KB" -> {
                bytes*=1024;
                return (long) bytes;
            }
            case "MB" -> {
                bytes*=1024*1024;
                return (long) bytes;
            }
            case "GB" -> {
                bytes*=1024*1024*1024;
                return (long) bytes;
            }
            default -> {
                return (long) bytes;
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton anonymousRb;
    private de.wannawork.jcalendar.JCalendarComboBox birthdateField;
    private javax.swing.JRadioButton downloadRb;
    private javax.swing.JComboBox<String> downloadUnit;
    private view.custom.textField emailField;
    private javax.swing.JRadioButton female;
    private view.custom.textField firstnameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private view.custom.textField lastnameField;
    private javax.swing.JRadioButton male;
    private view.custom.textField maxdownloadField;
    private view.custom.textField maxuploadField;
    private view.custom.textField quotaField;
    private javax.swing.JComboBox<String> quotaUnit;
    private view.custom.Button refreshBtn;
    private view.custom.Button renameConfirm;
    private javax.swing.JPanel renamePanel;
    private javax.swing.JLabel renameTitle;
    private javax.swing.JRadioButton uploadRb;
    private javax.swing.JComboBox<String> uploadUnit;
    private view.custom.textField usedField;
    private javax.swing.JTable userTable;
    // End of variables declaration//GEN-END:variables
}
