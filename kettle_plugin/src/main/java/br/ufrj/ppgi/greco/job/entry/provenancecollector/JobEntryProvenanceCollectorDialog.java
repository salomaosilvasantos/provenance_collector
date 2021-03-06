package br.ufrj.ppgi.greco.job.entry.provenancecollector;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.vfs.FileObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.ObjectLocationSpecificationMethod;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryDialogInterface;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.ui.core.dialog.ErrorDialog;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.job.dialog.JobDialog;
import org.pentaho.di.ui.job.entry.JobEntryDialog;
import org.pentaho.di.ui.repository.dialog.SelectObjectDialog;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import br.ufrj.ppgi.greco.job.entry.provenancecollector.finegrained.FineGrainedStep;
import br.ufrj.ppgi.greco.job.entry.provenancecollector.finegrained.FineGrainedStepMap;
import br.ufrj.ppgi.greco.job.entry.provenancecollector.util.EnumETLOperation;

/**
 * 
 * @author Rogers Reiche de Mendonca
 * @since nov-2012
 * 
 */
public class JobEntryProvenanceCollectorDialog extends JobEntryDialog implements
        JobEntryDialogInterface
{
    // for i18n purposes, needed by Translator2!! $NON-NLS-1$
    private static Class<?> PKG = JobEntryProvenanceCollector.class;

    private static final String[] FILE_FILTERLOGNAMES = new String[] {
            BaseMessages.getString(PKG, "JobJob.Fileformat.TXT"),
            BaseMessages.getString(PKG, "JobJob.Fileformat.LOG"),
            BaseMessages.getString(PKG, "JobJob.Fileformat.All") };

    private Label wlName;
    private Text wName;

    private Button wbJobname;
    private TextVar wJobname;

    private TextVar wDirectory;

    private Button wbFilename;
    private TextVar wFilename;

    private Composite wLogging;
    private FormData fdLogging;

    private Label wlSetLogfile;
    private Button wSetLogfile;
    private FormData fdlSetLogfile, fdSetLogfile;

    private Label wlLogfile;
    private TextVar wLogfile;
    private FormData fdlLogfile, fdLogfile;

    private Button wbLogFilename;
    private FormData fdbLogFilename;

    private Label wlCreateParentFolder;
    private Button wCreateParentFolder;
    private FormData fdlCreateParentFolder, fdCreateParentFolder;

    private Label wlLogext;
    private TextVar wLogext;
    private FormData fdlLogext, fdLogext;

    private Label wlAddDate;
    private Button wAddDate;
    private FormData fdlAddDate, fdAddDate;

    private Label wlAddTime;
    private Button wAddTime;
    private FormData fdlAddTime, fdAddTime;

    private Label wlLoglevel;
    private CCombo wLoglevel;
    private FormData fdlLoglevel, fdLoglevel;

    private Label wlPrevious;
    private Button wPrevious;
    private FormData fdlPrevious, fdPrevious;

    private Label wlPrevToParams;
    private Button wPrevToParams;
    private FormData fdlPrevToParams, fdPrevToParams;

    private Label wlEveryRow;
    private Button wEveryRow;
    private FormData fdlEveryRow, fdEveryRow;

    private TableView wFields;
    private TableView wParameters;

    private Label wlSlaveServer;
    private ComboVar wSlaveServer;
    private FormData fdlSlaveServer, fdSlaveServer;

    private Label wlPassExport;
    private Button wPassExport;
    private FormData fdlPassExport, fdPassExport;

    private Label wlWaitingToFinish;
    private Button wWaitingToFinish;
    private FormData fdlWaitingToFinish, fdWaitingToFinish;

    private Label wlFollowingAbortRemotely;
    private Button wFollowingAbortRemotely;
    private FormData fdlFollowingAbortRemotely, fdFollowingAbortRemotely;

    private Label wlPassParams;
    private Button wPassParams;
    private FormData fdlPassParams, fdPassParams;

    private Button wbGetParams;

    private Button wOK, wCancel;

    private Listener lsOK, lsCancel;

    private Shell shell;

    private SelectionAdapter lsDef;

    private JobEntryProvenanceCollector jobEntry;

    private boolean backupChanged;

    private Label wlAppendLogfile;

    private Button wAppendLogfile;

    private FormData fdlAppendLogfile, fdAppendLogfile;

    private Display display;

    private Button radioFilename;
    private Button radioByName;
    private Button radioByReference;

    private Button wbByReference;
    private Text wByReference;

    private Composite wAdvanced;

    private ObjectId referenceObjectId;

    private ObjectLocationSpecificationMethod specificationMethod;

    // Rogers (05/2013): Provenance Tab
    private Composite wProv;
    private CCombo wProvCmbConnection;
    private TableView wProvTblFGSteps;
    private final int COLUMN_STEP_ID = 1;
    private final int COLUMN_ETL_OPERATION = 2;

    private Group wProvGrpFGSteps;
    private Label wProvLblSelAll;
    private Button wProvChkSelAll;
    private Label wProvLblSelExtract;
    private Button wProvChkSelExtract;
    private Label wProvLblSelTransform;
    private Button wProvChkSelTransform;
    private Label wProvLblSelLoad;
    private Button wProvChkSelLoad;

    public JobEntryProvenanceCollectorDialog(Shell parent,
            JobEntryInterface jobEntryInt, Repository rep, JobMeta jobMeta)
    {
        super(parent, jobEntryInt, rep, jobMeta);
        jobEntry = (JobEntryProvenanceCollector) jobEntryInt;
    }

    private void selectFineGrainedStep(EnumETLOperation op, boolean enable)
    {
        for (TableItem item : wProvTblFGSteps.table.getItems())
        {
            if ((op == null)
                    || item.getText(COLUMN_ETL_OPERATION).equals(
                            op.getDescription()))
            {
                item.setChecked(enable);
            }
        }

        selectGroupEnabledFGStep(wProvTblFGSteps.table);
    }

    private boolean getChkSelSelected(Table table, EnumETLOperation op)
    {
        boolean selected = true;
        for (TableItem ti : table.getItems())
        {
            if (ti.getText(COLUMN_ETL_OPERATION).equals(op.getDescription())
                    && !ti.getChecked())
            {
                selected = false;
                break;
            }
        }
        return selected;
    }

    private void selectGroupEnabledFGStep(Table table)
    {
        boolean selected;

        // Extract
        selected = getChkSelSelected(table, EnumETLOperation.E);
        wProvChkSelExtract.setSelection(selected);

        // Transform
        selected = getChkSelSelected(table, EnumETLOperation.T);
        wProvChkSelTransform.setSelection(selected);

        // Load
        selected = getChkSelSelected(table, EnumETLOperation.L);
        wProvChkSelLoad.setSelection(selected);

        // All
        selectChkAll();
    }

    private void selectChkAll()
    {
        boolean selected = wProvChkSelExtract.getSelection()
                && wProvChkSelTransform.getSelection()
                && wProvChkSelLoad.getSelection();
        wProvChkSelAll.setSelection(selected);

    }

    private String lPad(String msgKey, int length, char chr)
    {
        StringBuilder ret = new StringBuilder(BaseMessages.getString(PKG,
                msgKey));
        for (int i = 0; i < length; ret.insert(0, chr), i++)
            ;
        return ret.toString();
    }

    public JobEntryInterface open()
    {
        Shell parent = getParent();
        display = parent.getDisplay();

        shell = new Shell(parent, props.getJobsDialogStyle());
        props.setLook(shell);
        JobDialog.setShellImage(shell, jobEntry);

        ModifyListener lsMod = new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                jobEntry.setChanged();
            }
        };
        backupChanged = jobEntry.hasChanged();

        FormLayout formLayout = new FormLayout();
        formLayout.marginWidth = Const.FORM_MARGIN;
        formLayout.marginHeight = Const.FORM_MARGIN;

        shell.setLayout(formLayout);
        shell.setText(BaseMessages.getString(PKG, "JobJob.Title"));

        int middle = props.getMiddlePct();
        int margin = Const.MARGIN;

        // Name line
        wlName = new Label(shell, SWT.RIGHT);
        wlName.setText(BaseMessages.getString(PKG, "JobJob.Name.Label"));
        props.setLook(wlName);
        FormData fdlName = new FormData();
        fdlName.left = new FormAttachment(0, 0);
        fdlName.top = new FormAttachment(0, 0);
        fdlName.right = new FormAttachment(middle, -margin);
        wlName.setLayoutData(fdlName);

        wName = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wName);
        wName.addModifyListener(lsMod);
        FormData fdName = new FormData();
        fdName.top = new FormAttachment(0, 0);
        fdName.left = new FormAttachment(middle, 0);
        fdName.right = new FormAttachment(100, 0);
        wName.setLayoutData(fdName);

        CTabFolder wTabFolder = new CTabFolder(shell, SWT.BORDER);
        props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);

        // Specification
        //
        CTabItem wSpecTab = new CTabItem(wTabFolder, SWT.NONE);
        wSpecTab.setText(BaseMessages.getString(PKG,
                "JobJob.Specification.Group.Label")); //$NON-NLS-1$

        ScrolledComposite wSSpec = new ScrolledComposite(wTabFolder,
                SWT.V_SCROLL | SWT.H_SCROLL);
        wSSpec.setLayout(new FillLayout());

        Composite wSpec = new Composite(wSSpec, SWT.SHADOW_NONE);
        props.setLook(wSpec);

        FormLayout specLayout = new FormLayout();
        specLayout.marginWidth = Const.FORM_MARGIN;
        specLayout.marginHeight = Const.FORM_MARGIN;
        wSpec.setLayout(specLayout);

        // The specify by filename option...
        //
        Group gFilename = new Group(wSpec, SWT.SHADOW_ETCHED_IN);
        props.setLook(gFilename);
        FormLayout gFileLayout = new FormLayout();
        gFileLayout.marginWidth = Const.FORM_MARGIN;
        gFileLayout.marginHeight = Const.FORM_MARGIN;
        gFilename.setLayout(gFileLayout);

        radioFilename = new Button(gFilename, SWT.RADIO);
        props.setLook(radioFilename);
        radioFilename.setText(BaseMessages.getString(PKG,
                "JobJob.JobFile.Label"));
        FormData fdRadioFilename = new FormData();
        fdRadioFilename.top = new FormAttachment(0, 0);
        fdRadioFilename.left = new FormAttachment(0, 0);
        fdRadioFilename.right = new FormAttachment(middle, -margin);
        radioFilename.setLayoutData(fdRadioFilename);
        radioFilename.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent arg0)
            {
                specificationMethod = ObjectLocationSpecificationMethod.FILENAME;
                setRadioButtons();
            }
        });

        wbFilename = new Button(gFilename, SWT.PUSH | SWT.CENTER);
        props.setLook(wbFilename);
        wbFilename.setImage(GUIResource.getInstance().getImageJobGraph());
        wbFilename.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.SelectJob.Tooltip"));
        FormData fdbFilename = new FormData();
        fdbFilename.top = new FormAttachment(0, 0);
        fdbFilename.right = new FormAttachment(100, 0);
        wbFilename.setLayoutData(fdbFilename);

        wFilename = new TextVar(jobMeta, gFilename, SWT.SINGLE | SWT.LEFT
                | SWT.BORDER);
        props.setLook(wFilename);
        wFilename.addModifyListener(lsMod);
        FormData fdFilename = new FormData();
        fdFilename.top = new FormAttachment(0, 0);
        fdFilename.left = new FormAttachment(middle, 0);
        fdFilename.right = new FormAttachment(wbFilename, -margin);
        wFilename.setLayoutData(fdFilename);
        wFilename.addModifyListener(new ModifyListener()
        {

            public void modifyText(ModifyEvent arg0)
            {
                specificationMethod = ObjectLocationSpecificationMethod.FILENAME;
                setRadioButtons();
            }
        });

        FormData fdgFilename = new FormData();
        fdgFilename.top = new FormAttachment(0, 0);
        fdgFilename.left = new FormAttachment(0, 0);
        fdgFilename.right = new FormAttachment(100, 0);
        gFilename.setLayoutData(fdgFilename);

        // The repository : specify by name radio option...
        //
        Group gByName = new Group(wSpec, SWT.SHADOW_ETCHED_IN);
        props.setLook(gByName);
        FormLayout gByNameLayout = new FormLayout();
        gByNameLayout.marginWidth = Const.FORM_MARGIN;
        gByNameLayout.marginHeight = Const.FORM_MARGIN;
        gByName.setLayout(gByNameLayout);

        radioByName = new Button(gByName, SWT.RADIO);
        props.setLook(radioByName);
        radioByName.setText(BaseMessages.getString(PKG,
                "JobJob.NameOfJob.Label"));
        FormData fdRadioByName = new FormData();
        fdRadioByName.top = new FormAttachment(0, 0);
        fdRadioByName.left = new FormAttachment(0, 0);
        fdRadioByName.right = new FormAttachment(middle, -margin);
        radioByName.setLayoutData(fdRadioByName);
        radioByName.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent arg0)
            {
                specificationMethod = ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME;
                setRadioButtons();
            }
        });

        wbJobname = new Button(gByName, SWT.PUSH | SWT.CENTER);
        props.setLook(wbJobname);
        wbJobname.setImage(GUIResource.getInstance().getImageJobGraph());
        wbJobname.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.SelectJobRep.Tooltip"));
        FormData fdbJobname = new FormData();
        fdbJobname.top = new FormAttachment(0, 0);
        fdbJobname.right = new FormAttachment(100, 0);
        wbJobname.setLayoutData(fdbJobname);

        wJobname = new TextVar(jobMeta, gByName, SWT.SINGLE | SWT.LEFT
                | SWT.BORDER);
        props.setLook(wJobname);
        wJobname.addModifyListener(lsMod);
        FormData fdJobname = new FormData();
        fdJobname.top = new FormAttachment(0, 0);
        fdJobname.left = new FormAttachment(middle, 0);
        fdJobname.right = new FormAttachment(wbJobname, -margin);
        wJobname.setLayoutData(fdJobname);

        wDirectory = new TextVar(jobMeta, gByName, SWT.SINGLE | SWT.LEFT
                | SWT.BORDER);
        props.setLook(wDirectory);
        wDirectory.addModifyListener(lsMod);
        FormData fdDirectory = new FormData();
        fdDirectory.top = new FormAttachment(wJobname, margin * 2);
        fdDirectory.left = new FormAttachment(middle, 0);
        fdDirectory.right = new FormAttachment(100, 0);
        wDirectory.setLayoutData(fdDirectory);

        FormData fdgByName = new FormData();
        fdgByName.top = new FormAttachment(gFilename, margin);
        fdgByName.left = new FormAttachment(0, 0);
        fdgByName.right = new FormAttachment(100, 0);
        gByName.setLayoutData(fdgByName);

        // The specify by filename option...
        //
        Group gByReference = new Group(wSpec, SWT.SHADOW_ETCHED_IN);
        props.setLook(gByReference);
        FormLayout gByReferenceLayout = new FormLayout();
        gByReferenceLayout.marginWidth = Const.FORM_MARGIN;
        gByReferenceLayout.marginHeight = Const.FORM_MARGIN;
        gByReference.setLayout(gByReferenceLayout);

        radioByReference = new Button(gByReference, SWT.RADIO);
        props.setLook(radioByReference);
        radioByReference.setText(BaseMessages.getString(PKG,
                "JobJob.JobByReference.Label"));
        FormData fdRadioByReference = new FormData();
        fdRadioByReference.top = new FormAttachment(0, 0);
        fdRadioByReference.left = new FormAttachment(0, 0);
        fdRadioByReference.right = new FormAttachment(middle, -margin);
        radioByReference.setLayoutData(fdRadioByReference);
        radioByReference.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent arg0)
            {
                specificationMethod = ObjectLocationSpecificationMethod.REPOSITORY_BY_REFERENCE;
                setRadioButtons();
            }
        });

        wbByReference = new Button(gByReference, SWT.PUSH | SWT.CENTER);
        props.setLook(wbByReference);
        wbByReference.setImage(GUIResource.getInstance().getImageJobGraph());
        wbByReference.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.SelectJob.Tooltip"));
        FormData fdbByReference = new FormData();
        fdbByReference.top = new FormAttachment(0, 0);
        fdbByReference.right = new FormAttachment(100, 0);
        wbByReference.setLayoutData(fdbByReference);

        wByReference = new Text(gByReference, SWT.SINGLE | SWT.LEFT
                | SWT.BORDER | SWT.READ_ONLY);
        props.setLook(wByReference);
        wByReference.addModifyListener(lsMod);
        FormData fdByReference = new FormData();
        fdByReference.top = new FormAttachment(0, 0);
        fdByReference.left = new FormAttachment(middle, 0);
        fdByReference.right = new FormAttachment(wbByReference, -margin);
        wByReference.setLayoutData(fdByReference);

        FormData fdgByReference = new FormData();
        fdgByReference.top = new FormAttachment(gByName, margin);
        fdgByReference.left = new FormAttachment(0, 0);
        fdgByReference.right = new FormAttachment(100, 0);
        gByReference.setLayoutData(fdgByReference);

        wSpec.pack();
        Rectangle bounds = wSpec.getBounds();

        wSSpec.setContent(wSpec);
        wSSpec.setExpandHorizontal(true);
        wSSpec.setExpandVertical(true);
        wSSpec.setMinWidth(bounds.width);
        wSSpec.setMinHeight(bounds.height);

        wSpecTab.setControl(wSSpec);

        FormData fdSpec = new FormData();
        fdSpec.left = new FormAttachment(0, 0);
        fdSpec.top = new FormAttachment(0, 0);
        fdSpec.right = new FormAttachment(100, 0);
        fdSpec.bottom = new FormAttachment(100, 0);
        wSpec.setLayoutData(fdSpec);

        // Advanced
        //
        CTabItem wAdvancedTab = new CTabItem(wTabFolder, SWT.NONE);
        wAdvancedTab.setText(BaseMessages.getString(PKG,
                "JobJob.Advanced.Group.Label")); //$NON-NLS-1$

        ScrolledComposite wSAdvanced = new ScrolledComposite(wTabFolder,
                SWT.V_SCROLL | SWT.H_SCROLL);
        wSAdvanced.setLayout(new FillLayout());

        wAdvanced = new Composite(wSAdvanced, SWT.SHADOW_NONE);
        props.setLook(wAdvanced);

        FormLayout advancedLayout = new FormLayout();
        advancedLayout.marginWidth = Const.FORM_MARGIN;
        advancedLayout.marginHeight = Const.FORM_MARGIN;
        wAdvanced.setLayout(advancedLayout);

        wlPrevious = new Label(wAdvanced, SWT.RIGHT);
        wlPrevious
                .setText(BaseMessages.getString(PKG, "JobJob.Previous.Label"));
        props.setLook(wlPrevious);
        fdlPrevious = new FormData();
        fdlPrevious.left = new FormAttachment(0, 0);
        fdlPrevious.top = new FormAttachment(0, 0);
        fdlPrevious.right = new FormAttachment(middle, -margin);
        wlPrevious.setLayoutData(fdlPrevious);
        wPrevious = new Button(wAdvanced, SWT.CHECK);
        props.setLook(wPrevious);
        wPrevious.setSelection(jobEntry.argFromPrevious);
        wPrevious.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.Previous.Tooltip"));
        fdPrevious = new FormData();
        fdPrevious.left = new FormAttachment(middle, 0);
        fdPrevious.top = new FormAttachment(0, 0);
        fdPrevious.right = new FormAttachment(100, 0);
        wPrevious.setLayoutData(fdPrevious);
        wPrevious.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                jobEntry.setChanged();
            }
        });

        wlPrevToParams = new Label(wAdvanced, SWT.RIGHT);
        wlPrevToParams.setText(BaseMessages.getString(PKG,
                "JobJob.PrevToParams.Label"));
        props.setLook(wlPrevToParams);
        fdlPrevToParams = new FormData();
        fdlPrevToParams.left = new FormAttachment(0, 0);
        fdlPrevToParams.top = new FormAttachment(wPrevious, margin * 3);
        fdlPrevToParams.right = new FormAttachment(middle, -margin);
        wlPrevToParams.setLayoutData(fdlPrevToParams);
        wPrevToParams = new Button(wAdvanced, SWT.CHECK);
        props.setLook(wPrevToParams);
        wPrevToParams.setSelection(jobEntry.paramsFromPrevious);
        wPrevToParams.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.PrevToParams.Tooltip"));
        fdPrevToParams = new FormData();
        fdPrevToParams.left = new FormAttachment(middle, 0);
        fdPrevToParams.top = new FormAttachment(wPrevious, margin * 3);
        fdPrevToParams.right = new FormAttachment(100, 0);
        wPrevToParams.setLayoutData(fdPrevToParams);
        wPrevToParams.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                jobEntry.setChanged();
            }
        });

        wlEveryRow = new Label(wAdvanced, SWT.RIGHT);
        wlEveryRow.setText(BaseMessages.getString(PKG,
                "JobJob.ExecForEveryInputRow.Label"));
        props.setLook(wlEveryRow);
        fdlEveryRow = new FormData();
        fdlEveryRow.left = new FormAttachment(0, 0);
        fdlEveryRow.top = new FormAttachment(wPrevToParams, margin * 3);
        fdlEveryRow.right = new FormAttachment(middle, -margin);
        wlEveryRow.setLayoutData(fdlEveryRow);
        wEveryRow = new Button(wAdvanced, SWT.CHECK);
        props.setLook(wEveryRow);
        wEveryRow.setSelection(jobEntry.execPerRow);
        wEveryRow.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.ExecForEveryInputRow.Tooltip"));
        fdEveryRow = new FormData();
        fdEveryRow.left = new FormAttachment(middle, 0);
        fdEveryRow.top = new FormAttachment(wPrevToParams, margin * 3);
        fdEveryRow.right = new FormAttachment(100, 0);
        wEveryRow.setLayoutData(fdEveryRow);
        wEveryRow.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                jobEntry.execPerRow = !jobEntry.execPerRow;
                jobEntry.setChanged();
            }
        });

        // The remote slave server
        wlSlaveServer = new Label(wAdvanced, SWT.RIGHT);
        wlSlaveServer.setText(BaseMessages.getString(PKG,
                "JobJob.SlaveServer.Label"));
        wlSlaveServer.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.SlaveServer.ToolTip"));
        props.setLook(wlSlaveServer);
        fdlSlaveServer = new FormData();
        fdlSlaveServer.left = new FormAttachment(0, 0);
        fdlSlaveServer.right = new FormAttachment(middle, -margin);
        fdlSlaveServer.top = new FormAttachment(wEveryRow, margin);
        wlSlaveServer.setLayoutData(fdlSlaveServer);
        wSlaveServer = new ComboVar(jobMeta, wAdvanced, SWT.SINGLE | SWT.BORDER);
        wSlaveServer.setItems(SlaveServer.getSlaveServerNames(jobMeta
                .getSlaveServers()));
        wSlaveServer.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.SlaveServer.ToolTip"));
        props.setLook(wSlaveServer);
        fdSlaveServer = new FormData();
        fdSlaveServer.left = new FormAttachment(middle, 0);
        fdSlaveServer.top = new FormAttachment(wEveryRow, margin);
        fdSlaveServer.right = new FormAttachment(100, 0);
        wSlaveServer.setLayoutData(fdSlaveServer);
        wSlaveServer.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                setActive();
            }
        });

        // Wait for the remote transformation to finish?
        //
        wlPassExport = new Label(wAdvanced, SWT.RIGHT);
        wlPassExport.setText(BaseMessages.getString(PKG,
                "JobJob.PassExportToSlave.Label"));
        props.setLook(wlPassExport);
        fdlPassExport = new FormData();
        fdlPassExport.left = new FormAttachment(0, 0);
        fdlPassExport.top = new FormAttachment(wSlaveServer, margin);
        fdlPassExport.right = new FormAttachment(middle, -margin);
        wlPassExport.setLayoutData(fdlPassExport);
        wPassExport = new Button(wAdvanced, SWT.CHECK);
        props.setLook(wPassExport);
        fdPassExport = new FormData();
        fdPassExport.left = new FormAttachment(middle, 0);
        fdPassExport.top = new FormAttachment(wSlaveServer, margin);
        fdPassExport.right = new FormAttachment(100, 0);
        wPassExport.setLayoutData(fdPassExport);

        // Wait for the remote transformation to finish?
        //
        wlWaitingToFinish = new Label(wAdvanced, SWT.RIGHT);
        wlWaitingToFinish.setText(BaseMessages.getString(PKG,
                "JobJob.WaitToFinish.Label"));
        props.setLook(wlWaitingToFinish);
        fdlWaitingToFinish = new FormData();
        fdlWaitingToFinish.left = new FormAttachment(0, 0);
        fdlWaitingToFinish.top = new FormAttachment(wPassExport, margin);
        fdlWaitingToFinish.right = new FormAttachment(middle, -margin);
        wlWaitingToFinish.setLayoutData(fdlWaitingToFinish);
        wWaitingToFinish = new Button(wAdvanced, SWT.CHECK);
        props.setLook(wWaitingToFinish);
        fdWaitingToFinish = new FormData();
        fdWaitingToFinish.left = new FormAttachment(middle, 0);
        fdWaitingToFinish.top = new FormAttachment(wPassExport, margin);
        fdWaitingToFinish.right = new FormAttachment(100, 0);
        wWaitingToFinish.setLayoutData(fdWaitingToFinish);
        wWaitingToFinish.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                setActive();
            }
        });

        // Follow a local abort remotely?
        //
        wlFollowingAbortRemotely = new Label(wAdvanced, SWT.RIGHT);
        wlFollowingAbortRemotely.setText(BaseMessages.getString(PKG,
                "JobJob.AbortRemote.Label"));
        props.setLook(wlFollowingAbortRemotely);
        fdlFollowingAbortRemotely = new FormData();
        fdlFollowingAbortRemotely.left = new FormAttachment(0, 0);
        fdlFollowingAbortRemotely.top = new FormAttachment(wWaitingToFinish,
                margin);
        fdlFollowingAbortRemotely.right = new FormAttachment(middle, -margin);
        wlFollowingAbortRemotely.setLayoutData(fdlFollowingAbortRemotely);
        wFollowingAbortRemotely = new Button(wAdvanced, SWT.CHECK);
        props.setLook(wFollowingAbortRemotely);
        fdFollowingAbortRemotely = new FormData();
        fdFollowingAbortRemotely.left = new FormAttachment(middle, 0);
        fdFollowingAbortRemotely.top = new FormAttachment(wWaitingToFinish,
                margin);
        fdFollowingAbortRemotely.right = new FormAttachment(100, 0);
        wFollowingAbortRemotely.setLayoutData(fdFollowingAbortRemotely);

        wAdvanced.pack();
        bounds = wAdvanced.getBounds();

        wSAdvanced.setContent(wAdvanced);
        wSAdvanced.setExpandHorizontal(true);
        wSAdvanced.setExpandVertical(true);
        wSAdvanced.setMinWidth(bounds.width);
        wSAdvanced.setMinHeight(bounds.height);

        wAdvancedTab.setControl(wSAdvanced);

        // Logging
        //
        CTabItem wLoggingTab = new CTabItem(wTabFolder, SWT.NONE);
        wLoggingTab.setText(BaseMessages.getString(PKG,
                "JobJob.LogSettings.Group.Label")); //$NON-NLS-1$

        ScrolledComposite wSLogging = new ScrolledComposite(wTabFolder,
                SWT.V_SCROLL | SWT.H_SCROLL);
        wSLogging.setLayout(new FillLayout());

        wLogging = new Composite(wSLogging, SWT.SHADOW_NONE);
        props.setLook(wLogging);

        FormLayout groupLayout = new FormLayout();
        groupLayout.marginWidth = Const.FORM_MARGIN;
        groupLayout.marginHeight = Const.FORM_MARGIN;

        wLogging.setLayout(groupLayout);

        // Set the logfile?
        //
        wlSetLogfile = new Label(wLogging, SWT.RIGHT);
        wlSetLogfile.setText(BaseMessages.getString(PKG,
                "JobJob.Specify.Logfile.Label"));
        props.setLook(wlSetLogfile);
        fdlSetLogfile = new FormData();
        fdlSetLogfile.left = new FormAttachment(0, 0);
        fdlSetLogfile.top = new FormAttachment(0, 0);
        fdlSetLogfile.right = new FormAttachment(middle, -margin);
        wlSetLogfile.setLayoutData(fdlSetLogfile);
        wSetLogfile = new Button(wLogging, SWT.CHECK);
        props.setLook(wSetLogfile);
        fdSetLogfile = new FormData();
        fdSetLogfile.left = new FormAttachment(middle, 0);
        fdSetLogfile.top = new FormAttachment(0, 0);
        fdSetLogfile.right = new FormAttachment(100, 0);
        wSetLogfile.setLayoutData(fdSetLogfile);
        wSetLogfile.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                setActive();
            }
        });

        // Append logfile?
        wlAppendLogfile = new Label(wLogging, SWT.RIGHT);
        wlAppendLogfile.setText(BaseMessages.getString(PKG,
                "JobJob.Append.Logfile.Label"));
        props.setLook(wlAppendLogfile);
        fdlAppendLogfile = new FormData();
        fdlAppendLogfile.left = new FormAttachment(0, 0);
        fdlAppendLogfile.top = new FormAttachment(wSetLogfile, margin);
        fdlAppendLogfile.right = new FormAttachment(middle, -margin);
        wlAppendLogfile.setLayoutData(fdlAppendLogfile);
        wAppendLogfile = new Button(wLogging, SWT.CHECK);
        wAppendLogfile.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.Append.Logfile.Tooltip"));
        props.setLook(wAppendLogfile);
        fdAppendLogfile = new FormData();
        fdAppendLogfile.left = new FormAttachment(middle, 0);
        fdAppendLogfile.top = new FormAttachment(wSetLogfile, margin);
        fdAppendLogfile.right = new FormAttachment(100, 0);
        wAppendLogfile.setLayoutData(fdAppendLogfile);
        wAppendLogfile.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
            }
        });

        // Set the logfile path + base-name
        wlLogfile = new Label(wLogging, SWT.RIGHT);
        wlLogfile.setText(BaseMessages.getString(PKG,
                "JobJob.NameOfLogfile.Label"));
        props.setLook(wlLogfile);
        fdlLogfile = new FormData();
        fdlLogfile.left = new FormAttachment(0, 0);
        fdlLogfile.top = new FormAttachment(wAppendLogfile, margin);
        fdlLogfile.right = new FormAttachment(middle, -margin);
        wlLogfile.setLayoutData(fdlLogfile);

        wbLogFilename = new Button(wLogging, SWT.PUSH | SWT.CENTER);
        props.setLook(wbLogFilename);
        wbLogFilename.setText(BaseMessages
                .getString(PKG, "JobJob.Browse.Label"));
        fdbLogFilename = new FormData();
        fdbLogFilename.top = new FormAttachment(wAppendLogfile, margin);
        fdbLogFilename.right = new FormAttachment(100, 0);
        wbLogFilename.setLayoutData(fdbLogFilename);

        wLogfile = new TextVar(jobMeta, wLogging, SWT.SINGLE | SWT.LEFT
                | SWT.BORDER);
        wLogfile.setText("");
        props.setLook(wLogfile);
        fdLogfile = new FormData();
        fdLogfile.left = new FormAttachment(middle, 0);
        fdLogfile.top = new FormAttachment(wAppendLogfile, margin);
        fdLogfile.right = new FormAttachment(wbLogFilename, -margin);
        wLogfile.setLayoutData(fdLogfile);

        // create parent folder?
        wlCreateParentFolder = new Label(wLogging, SWT.RIGHT);
        wlCreateParentFolder.setText(BaseMessages.getString(PKG,
                "JobJob.Logfile.CreateParentFolder.Label"));
        props.setLook(wlCreateParentFolder);
        fdlCreateParentFolder = new FormData();
        fdlCreateParentFolder.left = new FormAttachment(0, 0);
        fdlCreateParentFolder.top = new FormAttachment(wLogfile, margin);
        fdlCreateParentFolder.right = new FormAttachment(middle, -margin);
        wlCreateParentFolder.setLayoutData(fdlCreateParentFolder);
        wCreateParentFolder = new Button(wLogging, SWT.CHECK);
        wCreateParentFolder.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.Logfile.CreateParentFolder.Tooltip"));
        props.setLook(wCreateParentFolder);
        fdCreateParentFolder = new FormData();
        fdCreateParentFolder.left = new FormAttachment(middle, 0);
        fdCreateParentFolder.top = new FormAttachment(wLogfile, margin);
        fdCreateParentFolder.right = new FormAttachment(100, 0);
        wCreateParentFolder.setLayoutData(fdCreateParentFolder);
        wCreateParentFolder.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
            }
        });

        // Set the logfile filename extention
        wlLogext = new Label(wLogging, SWT.RIGHT);
        wlLogext.setText(BaseMessages.getString(PKG,
                "JobJob.LogfileExtension.Label"));
        props.setLook(wlLogext);
        fdlLogext = new FormData();
        fdlLogext.left = new FormAttachment(0, 0);
        fdlLogext.top = new FormAttachment(wCreateParentFolder, margin);
        fdlLogext.right = new FormAttachment(middle, -margin);
        wlLogext.setLayoutData(fdlLogext);
        wLogext = new TextVar(jobMeta, wLogging, SWT.SINGLE | SWT.LEFT
                | SWT.BORDER);
        wLogext.setText("");
        props.setLook(wLogext);
        fdLogext = new FormData();
        fdLogext.left = new FormAttachment(middle, 0);
        fdLogext.top = new FormAttachment(wCreateParentFolder, margin);
        fdLogext.right = new FormAttachment(100, 0);
        wLogext.setLayoutData(fdLogext);

        // Add date to logfile name?
        wlAddDate = new Label(wLogging, SWT.RIGHT);
        wlAddDate.setText(BaseMessages.getString(PKG,
                "JobJob.Logfile.IncludeDate.Label"));
        props.setLook(wlAddDate);
        fdlAddDate = new FormData();
        fdlAddDate.left = new FormAttachment(0, 0);
        fdlAddDate.top = new FormAttachment(wLogext, margin);
        fdlAddDate.right = new FormAttachment(middle, -margin);
        wlAddDate.setLayoutData(fdlAddDate);
        wAddDate = new Button(wLogging, SWT.CHECK);
        props.setLook(wAddDate);
        fdAddDate = new FormData();
        fdAddDate.left = new FormAttachment(middle, 0);
        fdAddDate.top = new FormAttachment(wLogext, margin);
        fdAddDate.right = new FormAttachment(100, 0);
        wAddDate.setLayoutData(fdAddDate);

        // Add time to logfile name?
        wlAddTime = new Label(wLogging, SWT.RIGHT);
        wlAddTime.setText(BaseMessages.getString(PKG,
                "JobJob.Logfile.IncludeTime.Label"));
        props.setLook(wlAddTime);
        fdlAddTime = new FormData();
        fdlAddTime.left = new FormAttachment(0, 0);
        fdlAddTime.top = new FormAttachment(wAddDate, margin);
        fdlAddTime.right = new FormAttachment(middle, -margin);
        wlAddTime.setLayoutData(fdlAddTime);
        wAddTime = new Button(wLogging, SWT.CHECK);
        props.setLook(wAddTime);
        fdAddTime = new FormData();
        fdAddTime.left = new FormAttachment(middle, 0);
        fdAddTime.top = new FormAttachment(wAddDate, margin);
        fdAddTime.right = new FormAttachment(100, 0);
        wAddTime.setLayoutData(fdAddTime);

        wlLoglevel = new Label(wLogging, SWT.RIGHT);
        wlLoglevel
                .setText(BaseMessages.getString(PKG, "JobJob.Loglevel.Label"));
        props.setLook(wlLoglevel);
        fdlLoglevel = new FormData();
        fdlLoglevel.left = new FormAttachment(0, 0);
        fdlLoglevel.right = new FormAttachment(middle, -margin);
        fdlLoglevel.top = new FormAttachment(wAddTime, margin);
        wlLoglevel.setLayoutData(fdlLoglevel);
        wLoglevel = new CCombo(wLogging, SWT.SINGLE | SWT.READ_ONLY
                | SWT.BORDER);
        wLoglevel.setItems(LogLevel.getLogLevelDescriptions());
        props.setLook(wLoglevel);
        fdLoglevel = new FormData();
        fdLoglevel.left = new FormAttachment(middle, 0);
        fdLoglevel.top = new FormAttachment(wAddTime, margin);
        fdLoglevel.right = new FormAttachment(100, 0);
        wLoglevel.setLayoutData(fdLoglevel);

        fdLogging = new FormData();
        fdLogging.left = new FormAttachment(0, margin);
        fdLogging.top = new FormAttachment(wbFilename, margin);
        fdLogging.right = new FormAttachment(100, -margin);
        wLogging.setLayoutData(fdLogging);

        wLogging.pack();
        bounds = wLogging.getBounds();

        wSLogging.setContent(wLogging);
        wSLogging.setExpandHorizontal(true);
        wSLogging.setExpandVertical(true);
        wSLogging.setMinWidth(bounds.width);
        wSLogging.setMinHeight(bounds.height);

        wLoggingTab.setControl(wSLogging);

        // Arguments
        //
        CTabItem wFieldTab = new CTabItem(wTabFolder, SWT.NONE);
        wFieldTab.setText(BaseMessages.getString(PKG,
                "JobJob.Fields.Argument.Label")); //$NON-NLS-1$

        FormLayout fieldLayout = new FormLayout();
        fieldLayout.marginWidth = Const.MARGIN;
        fieldLayout.marginHeight = Const.MARGIN;

        Composite wFieldComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wFieldComp);
        wFieldComp.setLayout(fieldLayout);

        final int FieldsCols = 1;
        int rows = jobEntry.arguments == null ? 1
                : (jobEntry.arguments.length == 0 ? 0
                        : jobEntry.arguments.length);
        final int FieldsRows = rows;

        ColumnInfo[] colinf = new ColumnInfo[FieldsCols];
        colinf[0] = new ColumnInfo(BaseMessages.getString(PKG,
                "JobJob.Fields.Argument.Label"), ColumnInfo.COLUMN_TYPE_TEXT,
                false);
        colinf[0].setUsingVariables(true);

        wFields = new TableView(jobMeta, wFieldComp, SWT.BORDER
                | SWT.FULL_SELECTION | SWT.MULTI, colinf, FieldsRows, lsMod,
                props);

        FormData fdFields = new FormData();
        fdFields.left = new FormAttachment(0, 0);
        fdFields.top = new FormAttachment(0, margin);
        fdFields.right = new FormAttachment(100, 0);
        fdFields.bottom = new FormAttachment(100, 0);
        wFields.setLayoutData(fdFields);

        FormData fdFieldsComp = new FormData();
        fdFieldsComp.left = new FormAttachment(0, 0);
        fdFieldsComp.top = new FormAttachment(0, 0);
        fdFieldsComp.right = new FormAttachment(100, 0);
        fdFieldsComp.bottom = new FormAttachment(100, 0);
        wFieldComp.setLayoutData(fdFieldsComp);

        wFieldComp.layout();
        wFieldTab.setControl(wFieldComp);

        // The parameters tab
        CTabItem wParametersTab = new CTabItem(wTabFolder, SWT.NONE);
        wParametersTab.setText(BaseMessages.getString(PKG,
                "JobJob.Fields.Parameters.Label")); //$NON-NLS-1$

        fieldLayout = new FormLayout();
        fieldLayout.marginWidth = Const.MARGIN;
        fieldLayout.marginHeight = Const.MARGIN;

        Composite wParameterComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wParameterComp);
        wParameterComp.setLayout(fieldLayout);

        // Pass all parameters down
        //
        wlPassParams = new Label(wParameterComp, SWT.RIGHT);
        wlPassParams.setText(BaseMessages.getString(PKG,
                "JobJob.PassAllParameters.Label"));
        props.setLook(wlPassParams);
        fdlPassParams = new FormData();
        fdlPassParams.left = new FormAttachment(0, 0);
        fdlPassParams.top = new FormAttachment(0, 0);
        fdlPassParams.right = new FormAttachment(middle, -margin);
        wlPassParams.setLayoutData(fdlPassParams);
        wPassParams = new Button(wParameterComp, SWT.CHECK);
        props.setLook(wPassParams);
        fdPassParams = new FormData();
        fdPassParams.left = new FormAttachment(middle, 0);
        fdPassParams.top = new FormAttachment(0, 0);
        fdPassParams.right = new FormAttachment(100, 0);
        wPassParams.setLayoutData(fdPassParams);

        wbGetParams = new Button(wParameterComp, SWT.PUSH);
        wbGetParams.setText(BaseMessages.getString(PKG,
                "JobJob.GetParameters.Button.Label"));
        FormData fdGetParams = new FormData();
        fdGetParams.top = new FormAttachment(wPassParams, margin);
        fdGetParams.right = new FormAttachment(100, 0);
        wbGetParams.setLayoutData(fdGetParams);
        wbGetParams.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent arg0)
            {
                getParameters();
            }
        });

        final int parameterRows = jobEntry.parameters == null ? 0
                : jobEntry.parameters.length;

        colinf = new ColumnInfo[] {
                new ColumnInfo(BaseMessages.getString(PKG,
                        "JobJob.Parameters.Parameter.Label"),
                        ColumnInfo.COLUMN_TYPE_TEXT, false),
                new ColumnInfo(BaseMessages.getString(PKG,
                        "JobJob.Parameters.ColumnName.Label"),
                        ColumnInfo.COLUMN_TYPE_TEXT, false),
                new ColumnInfo(BaseMessages.getString(PKG,
                        "JobJob.Parameters.Value.Label"),
                        ColumnInfo.COLUMN_TYPE_TEXT, false), };
        colinf[2].setUsingVariables(true);

        wParameters = new TableView(jobMeta, wParameterComp, SWT.BORDER
                | SWT.FULL_SELECTION | SWT.MULTI, colinf, parameterRows, lsMod,
                props);

        FormData fdParameters = new FormData();
        fdParameters.left = new FormAttachment(0, 0);
        fdParameters.top = new FormAttachment(wPassParams, margin);
        fdParameters.right = new FormAttachment(wbGetParams, -margin);
        fdParameters.bottom = new FormAttachment(100, 0);
        wParameters.setLayoutData(fdParameters);

        FormData fdParametersComp = new FormData();
        fdParametersComp.left = new FormAttachment(0, 0);
        fdParametersComp.top = new FormAttachment(0, 0);
        fdParametersComp.right = new FormAttachment(100, 0);
        fdParametersComp.bottom = new FormAttachment(100, 0);
        wParameterComp.setLayoutData(fdParametersComp);

        wParameterComp.layout();
        wParametersTab.setControl(wParameterComp);

        /*
         * Rogers (05/2013): Provenance Tab (INICIO)
         */
        CTabItem wProvTab = new CTabItem(wTabFolder, SWT.NONE);
        wProvTab.setText(BaseMessages.getString(PKG,
                "JobJob.Provenance.Group.Label")); //$NON-NLS-1$

        ScrolledComposite wSProv = new ScrolledComposite(wTabFolder,
                SWT.V_SCROLL | SWT.H_SCROLL);
        wSProv.setLayout(new FillLayout());

        wProv = new Composite(wSProv, SWT.SHADOW_NONE);
        props.setLook(wProv);

        FormLayout provLayout = new FormLayout();
        provLayout.marginWidth = Const.FORM_MARGIN;
        provLayout.marginHeight = Const.FORM_MARGIN;
        wProv.setLayout(provLayout);

        // Connection line
        wProvCmbConnection = addConnectionLine(wProv, null, middle, margin);
        if (jobEntry.getProvConnection() == null && jobMeta.nrDatabases() == 1)
            wProvCmbConnection.select(0);
        wProvCmbConnection.addModifyListener(lsMod);

        // Group of enabling fine-grained steps
        // Group
        wProvGrpFGSteps = new Group(wProv, SWT.SHADOW_NONE);
        props.setLook(wProvGrpFGSteps);
        wProvGrpFGSteps.setText(BaseMessages.getString(PKG,
                "JobJob.Provenance.Selection.Enabled.Steps.Group.Label"));

        RowLayout rowLayout = new RowLayout();
        rowLayout.wrap = true;
        rowLayout.pack = true;
        rowLayout.justify = false;
        rowLayout.type = SWT.HORIZONTAL;
        rowLayout.marginLeft = 10;
        rowLayout.marginTop = 10;
        rowLayout.marginRight = 10;
        rowLayout.marginBottom = 10;
        rowLayout.spacing = 3;
        wProvGrpFGSteps.setLayout(rowLayout);

        FormData fdProvEnabledFGSteps = new FormData();
        fdProvEnabledFGSteps.left = new FormAttachment(0, margin);
        fdProvEnabledFGSteps.top = new FormAttachment(wProvCmbConnection,
                margin);
        fdProvEnabledFGSteps.right = new FormAttachment(100, -margin);
        wProvGrpFGSteps.setLayoutData(fdProvEnabledFGSteps);

        // Check All
        wProvLblSelAll = new Label(wProvGrpFGSteps, SWT.PUSH);
        wProvLblSelAll.setText(BaseMessages.getString(PKG,
                "JobJob.Provenance.Fine.Grained.Select.All.Label"));
        props.setLook(wProvLblSelAll);
        wProvChkSelAll = new Button(wProvGrpFGSteps, SWT.CHECK);
        props.setLook(wProvChkSelAll);
        wProvChkSelAll.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.Provenance.Fine.Grained.Select.All.Tooltip"));
        wProvChkSelAll.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                selectFineGrainedStep(null, wProvChkSelAll.getSelection());
            }
        });

        int GAP = 10;
        char SPACE = 32;
        // Check Extraction
        wProvLblSelExtract = new Label(wProvGrpFGSteps, SWT.PUSH);
        String lblExtract = lPad(
                "JobJob.Provenance.Fine.Grained.Select.Extraction.Label", GAP,
                SPACE);
        wProvLblSelExtract.setText(lblExtract);
        props.setLook(wProvLblSelExtract);
        wProvChkSelExtract = new Button(wProvGrpFGSteps, SWT.CHECK);
        props.setLook(wProvChkSelExtract);
        wProvChkSelExtract.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.Provenance.Fine.Grained.Select.Extraction.Tooltip"));
        wProvChkSelExtract.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                selectFineGrainedStep(EnumETLOperation.E,
                        wProvChkSelExtract.getSelection());
            }
        });

        // Check Transformation
        wProvLblSelTransform = new Label(wProvGrpFGSteps, SWT.PUSH);
        String lblTransf = lPad(
                "JobJob.Provenance.Fine.Grained.Select.Transformation.Label",
                GAP, SPACE);
        wProvLblSelTransform.setText(lblTransf);
        props.setLook(wProvLblSelTransform);
        wProvChkSelTransform = new Button(wProvGrpFGSteps, SWT.CHECK);
        props.setLook(wProvChkSelTransform);
        wProvChkSelTransform
                .setToolTipText(BaseMessages
                        .getString(PKG,
                                "JobJob.Provenance.Fine.Grained.Select.Transformation.Tooltip"));
        wProvChkSelTransform.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                selectFineGrainedStep(EnumETLOperation.T,
                        wProvChkSelTransform.getSelection());
            }
        });

        // Check Load
        wProvLblSelLoad = new Label(wProvGrpFGSteps, SWT.PUSH);
        String lblLoad = lPad(
                "JobJob.Provenance.Fine.Grained.Select.Load.Label", GAP, SPACE);
        wProvLblSelLoad.setText(lblLoad);
        props.setLook(wProvLblSelLoad);
        wProvChkSelLoad = new Button(wProvGrpFGSteps, SWT.CHECK);
        props.setLook(wProvChkSelLoad);
        wProvChkSelLoad.setToolTipText(BaseMessages.getString(PKG,
                "JobJob.Provenance.Fine.Grained.Select.Load.Tooltip"));
        wProvChkSelLoad.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                selectFineGrainedStep(EnumETLOperation.L,
                        wProvChkSelLoad.getSelection());
            }
        });

        // Fine-Grained Provenance Steps grid...
        Label wlFGSteps = new Label(wProv, SWT.NONE);
        wlFGSteps.setText(BaseMessages.getString(PKG,
                "JobJob.Provenance.Fine.Grained.Steps.Label")); //$NON-NLS-1$
        props.setLook(wlFGSteps);
        FormData fdlFGSteps = new FormData();
        fdlFGSteps.left = new FormAttachment(0, 0);
        fdlFGSteps.top = new FormAttachment(wProvGrpFGSteps, margin * 2);
        wlFGSteps.setLayoutData(fdlFGSteps);

        final int nrRows = FineGrainedStepMap.get().size();

        colinf = new ColumnInfo[] {
                new ColumnInfo(
                        BaseMessages.getString(PKG,
                                "JobJob.Provenance.Step.Grid.StepType"), ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
                new ColumnInfo(
                        BaseMessages.getString(PKG,
                                "JobJob.Provenance.Step.Grid.ETLOperation"), ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$
                new ColumnInfo(
                        BaseMessages.getString(PKG,
                                "JobJob.Provenance.Step.Grid.Description"), ColumnInfo.COLUMN_TYPE_TEXT, false, true), //$NON-NLS-1$                                
        };

        wProvTblFGSteps = new TableView(jobMeta, wProv, SWT.BORDER
                | SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK, colinf, nrRows,
                true, lsMod, props);

        wProvTblFGSteps.setSortable(false);
        wProvTblFGSteps.table.getColumn(0).setText(
                BaseMessages.getString(PKG,
                        "JobJob.Provenance.Step.Grid.Enabled"));

        int i = 0;
        Map<String, FineGrainedStep> fineGrainedMap = FineGrainedStepMap.get()
                .getFineGrainedStepMap();
        for (Map.Entry<String, FineGrainedStep> entry : fineGrainedMap
                .entrySet())
        {
            FineGrainedStep stepType = entry.getValue();
            TableItem item = wProvTblFGSteps.table.getItem(i);
            item.setText(0, "");
            item.setText(1, stepType.getId());
            item.setBackground(1, GUIResource.getInstance().getColorLightGray());
            item.setText(2, stepType.getOperation().getDescription());
            item.setBackground(2, GUIResource.getInstance().getColorLightGray());
            item.setText(3, stepType.getDescription());
            item.setBackground(3, GUIResource.getInstance().getColorLightGray());

            i++;
        }
        wProvTblFGSteps.table.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                selectGroupEnabledFGStep(wProvTblFGSteps.table);
            }
        });

        FormData fdOptionFields = new FormData();
        fdOptionFields.left = new FormAttachment(0, 0);
        fdOptionFields.top = new FormAttachment(wlFGSteps, margin);
        fdOptionFields.right = new FormAttachment(100, 0);
        fdOptionFields.bottom = new FormAttachment(100, 0);
        wProvTblFGSteps.setLayoutData(fdOptionFields);

        wProvTblFGSteps.optWidth(true);
        wProvTblFGSteps.layout();

        // Finalization
        wProv.pack();
        bounds = wProv.getBounds();

        wSProv.setContent(wProv);
        wSProv.setExpandHorizontal(true);
        wSProv.setExpandVertical(true);
        wSProv.setMinWidth(bounds.width);
        wSProv.setMinHeight(bounds.height);

        wProvTab.setControl(wSProv);
        /*
         * Rogers (05/2013): Provenance Tab (FIM)
         */

        FormData fdTabFolder = new FormData();
        fdTabFolder.left = new FormAttachment(0, 0);
        fdTabFolder.top = new FormAttachment(wName, margin * 3);
        fdTabFolder.right = new FormAttachment(100, 0);
        fdTabFolder.bottom = new FormAttachment(100, -50);
        wTabFolder.setLayoutData(fdTabFolder);

        wTabFolder.setSelection(0);

        // Some buttons
        wOK = new Button(shell, SWT.PUSH);
        wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
        wCancel = new Button(shell, SWT.PUSH);
        wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

        BaseStepDialog.positionBottomButtons(shell,
                new Button[] { wOK, wCancel }, margin, wTabFolder);

        // Add listeners
        lsCancel = new Listener()
        {
            public void handleEvent(Event e)
            {
                cancel();
            }
        };
        lsOK = new Listener()
        {
            public void handleEvent(Event e)
            {
                ok();
            }
        };

        wOK.addListener(SWT.Selection, lsOK);
        wCancel.addListener(SWT.Selection, lsCancel);

        lsDef = new SelectionAdapter()
        {
            public void widgetDefaultSelected(SelectionEvent e)
            {
                ok();
            }
        };
        wName.addSelectionListener(lsDef);
        wFilename.addSelectionListener(lsDef);

        wbJobname.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                selectJob();
            }
        });

        wbFilename.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                pickFileVFS();
            }
        });

        wbByReference.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                selectJobByReference();
            }
        });

        wbLogFilename.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {

                FileDialog dialog = new FileDialog(shell, SWT.SAVE);
                dialog.setFilterExtensions(new String[] { "*.txt", "*.log", "*" });
                dialog.setFilterNames(FILE_FILTERLOGNAMES);

                if (wLogfile.getText() != null)
                    dialog.setFileName(jobMeta.environmentSubstitute(wLogfile
                            .getText()));

                if (dialog.open() != null)
                {
                    wLogfile.setText(dialog.getFilterPath()
                            + Const.FILE_SEPARATOR + dialog.getFileName());
                    String filename = dialog.getFilterPath()
                            + Const.FILE_SEPARATOR + dialog.getFileName();
                    FileObject file = null;
                    try
                    {
                        file = KettleVFS.getFileObject(filename);
                        // Set file extension ..
                        wLogext.setText(file.getName().getExtension());
                        // Set filename without extension ...
                        wLogfile.setText(wLogfile.getText().substring(
                                0,
                                wLogfile.getText().length()
                                        - wLogext.getText().length() - 1));
                    }
                    catch (Exception ex)
                    {
                    }
                    ;
                    if (file != null)
                    {
                        try
                        {
                            file.close();
                        }
                        catch (IOException ex)
                        {
                        }
                        ;
                    }
                }

            }
        });

        // Detect [X] or ALT-F4 or something that kills this window...
        shell.addShellListener(new ShellAdapter()
        {
            public void shellClosed(ShellEvent e)
            {
                cancel();
            }
        });

        getData();
        setActive();

        BaseStepDialog.setSize(shell);

        shell.open();
        props.setDialogSize(shell, "JobJobDialogSize");
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        return jobEntry;
    }

    protected void getParameters()
    {
        try
        {
            JobEntryProvenanceCollector jej = new JobEntryProvenanceCollector();
            getInfo(jej);
            JobMeta jm = jej.getJobMeta(rep, jobMeta);
            String[] parameters = jm.listParameters();

            String[] existing = wParameters.getItems(1);

            for (int i = 0; i < parameters.length; i++)
            {
                if (Const.indexOfString(parameters[i], existing) < 0)
                {
                    TableItem item = new TableItem(wParameters.table, SWT.NONE);
                    item.setText(1, parameters[i]);
                }
            }
            wParameters.removeEmptyRows();
            wParameters.setRowNums();
            wParameters.optWidth(true);
        }
        catch (Exception e)
        {
            new ErrorDialog(
                    shell,
                    BaseMessages
                            .getString(PKG,
                                    "JobEntryJobDialog.Exception.UnableToLoadJob.Title"),
                    BaseMessages
                            .getString(PKG,
                                    "JobEntryJobDialog.Exception.UnableToLoadJob.Message"),
                    e);
        }
    }

    protected void selectJob()
    {
        if (rep != null)
        {
            SelectObjectDialog sod = new SelectObjectDialog(shell, rep, false,
                    true);
            String jobname = sod.open();
            if (jobname != null)
            {
                wJobname.setText(jobname);
                wDirectory.setText(sod.getDirectory().getPath());
                // Copy it to the job entry name too...
                wName.setText(wJobname.getText());
                specificationMethod = ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME;
                setRadioButtons();
            }
        }
    }

    protected void setRadioButtons()
    {
        radioFilename
                .setSelection(specificationMethod == ObjectLocationSpecificationMethod.FILENAME);
        radioByName
                .setSelection(specificationMethod == ObjectLocationSpecificationMethod.REPOSITORY_BY_NAME);
        radioByReference
                .setSelection(specificationMethod == ObjectLocationSpecificationMethod.REPOSITORY_BY_REFERENCE);
        setActive();
    }

    private void getByReferenceData(RepositoryElementMetaInterface jobInf)
    {
        String path = jobInf.getRepositoryDirectory().getPath();
        if (!path.endsWith("/"))
            path += "/";
        path += jobInf.getName();
        wByReference.setText(path);
    }

    protected void selectJobByReference()
    {
        if (rep != null)
        {
            SelectObjectDialog sod = new SelectObjectDialog(shell, rep, false,
                    true);
            sod.open();
            RepositoryElementMetaInterface repositoryObject = sod
                    .getRepositoryObject();
            if (repositoryObject != null)
            {
                getByReferenceData(repositoryObject);
                referenceObjectId = repositoryObject.getObjectId();
                specificationMethod = ObjectLocationSpecificationMethod.REPOSITORY_BY_REFERENCE;
                setRadioButtons();
            }
        }
    }

    protected void pickFileVFS()
    {

        FileDialog dialog = new FileDialog(shell, SWT.OPEN);
        dialog.setFilterExtensions(Const.STRING_JOB_FILTER_EXT);
        dialog.setFilterNames(Const.getJobFilterNames());
        String prevName = jobMeta.environmentSubstitute(wFilename.getText());
        String parentFolder = null;
        try
        {
            parentFolder = KettleVFS.getFilename(KettleVFS.getFileObject(
                    jobMeta.environmentSubstitute(jobMeta.getFilename()))
                    .getParent());
        }
        catch (Exception e)
        {
            // not that important
        }
        if (!Const.isEmpty(prevName))
        {
            try
            {
                if (KettleVFS.fileExists(prevName))
                {
                    dialog.setFilterPath(KettleVFS.getFilename(KettleVFS
                            .getFileObject(prevName).getParent()));
                }
                else
                {

                    if (!prevName.endsWith(".kjb"))
                    {
                        prevName = "${"
                                + Const.INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY
                                + "}/" + Const.trim(wFilename.getText())
                                + ".kjb";
                    }
                    if (KettleVFS.fileExists(prevName))
                    {
                        wFilename.setText(prevName);
                        specificationMethod = ObjectLocationSpecificationMethod.FILENAME;
                        setRadioButtons();
                        return;
                    }
                    else
                    {
                        // File specified doesn't exist. Ask if we should create
                        // the file...
                        //
                        MessageBox mb = new MessageBox(shell, SWT.YES | SWT.NO
                                | SWT.ICON_QUESTION);
                        mb.setMessage(BaseMessages.getString(PKG,
                                "JobJob.Dialog.CreateJobQuestion.Message"));
                        mb.setText(BaseMessages.getString(PKG,
                                "JobJob.Dialog.CreateJobQuestion.Title")); // Sorry!
                        int answer = mb.open();
                        if (answer == SWT.YES)
                        {

                            Spoon spoon = Spoon.getInstance();
                            spoon.newJobFile();
                            JobMeta newJobMeta = spoon.getActiveJob();
                            newJobMeta.initializeVariablesFrom(jobEntry);
                            newJobMeta.setFilename(jobMeta
                                    .environmentSubstitute(prevName));
                            wFilename.setText(prevName);
                            specificationMethod = ObjectLocationSpecificationMethod.FILENAME;
                            setRadioButtons();
                            spoon.saveFile();
                            return;
                        }
                    }
                }
            }
            catch (Exception e)
            {
                dialog.setFilterPath(parentFolder);
            }
        }
        else if (!Const.isEmpty(parentFolder))
        {
            dialog.setFilterPath(parentFolder);
        }

        String fname = dialog.open();
        if (fname != null)
        {
            File file = new File(fname);
            String name = file.getName();
            String parentFolderSelection = file.getParentFile().toString();

            if (!Const.isEmpty(parentFolder)
                    && parentFolder.equals(parentFolderSelection))
            {
                wFilename.setText("${"
                        + Const.INTERNAL_VARIABLE_JOB_FILENAME_DIRECTORY + "}/"
                        + name);
            }
            else
            {
                wFilename.setText(fname);
            }

        }
    }

    public void dispose()
    {
        WindowProperty winprop = new WindowProperty(shell);
        props.setScreen(winprop);
        shell.dispose();
    }

    public void setActive()
    {
        radioByName.setEnabled(rep != null);
        radioByReference.setEnabled(rep != null);
        wFilename.setEnabled(radioFilename.getSelection());
        wbLogFilename.setEnabled(wSetLogfile.getSelection());
        wJobname.setEnabled(rep != null && radioByName.getSelection());
        wbJobname.setEnabled(rep != null);
        wDirectory.setEnabled(rep != null && radioByName.getSelection());

        wByReference.setEnabled(rep != null && radioByReference.getSelection());
        wbByReference.setEnabled(rep != null);

        wlLogfile.setEnabled(wSetLogfile.getSelection());
        wLogfile.setEnabled(wSetLogfile.getSelection());

        wlLogext.setEnabled(wSetLogfile.getSelection());
        wLogext.setEnabled(wSetLogfile.getSelection());

        wlCreateParentFolder.setEnabled(wSetLogfile.getSelection());
        wCreateParentFolder.setEnabled(wSetLogfile.getSelection());

        wlAddDate.setEnabled(wSetLogfile.getSelection());
        wAddDate.setEnabled(wSetLogfile.getSelection());

        wlAddTime.setEnabled(wSetLogfile.getSelection());
        wAddTime.setEnabled(wSetLogfile.getSelection());

        wlLoglevel.setEnabled(wSetLogfile.getSelection());
        wLoglevel.setEnabled(wSetLogfile.getSelection());

        wlAppendLogfile.setEnabled(wSetLogfile.getSelection());
        wAppendLogfile.setEnabled(wSetLogfile.getSelection());

        wlWaitingToFinish.setEnabled(!Const.isEmpty(wSlaveServer.getText()));
        wWaitingToFinish.setEnabled(!Const.isEmpty(wSlaveServer.getText()));

        wlFollowingAbortRemotely.setEnabled(wWaitingToFinish.getSelection()
                && !Const.isEmpty(wSlaveServer.getText()));
        wFollowingAbortRemotely.setEnabled(wWaitingToFinish.getSelection()
                && !Const.isEmpty(wSlaveServer.getText()));
    }

    public void getData()
    {
        wName.setText(Const.NVL(jobEntry.getName(), ""));

        specificationMethod = jobEntry.getSpecificationMethod();
        switch (specificationMethod)
        {
            case FILENAME:
                wFilename.setText(Const.NVL(jobEntry.getFilename(), ""));
                break;
            case REPOSITORY_BY_NAME:
                wDirectory.setText(Const.NVL(jobEntry.getDirectory(), ""));
                wJobname.setText(Const.NVL(jobEntry.getJobName(), ""));
                break;
            case REPOSITORY_BY_REFERENCE:
                referenceObjectId = jobEntry.getJobObjectId();
                wByReference.setText("");
                if (rep != null)
                {
                    try
                    {
                        RepositoryObject jobInf = rep.getObjectInformation(
                                jobEntry.getJobObjectId(),
                                RepositoryObjectType.JOB);
                        if (jobInf != null)
                        {
                            getByReferenceData(jobInf);
                        }
                    }
                    catch (KettleException e)
                    {
                        new ErrorDialog(
                                shell,
                                BaseMessages
                                        .getString(PKG,
                                                "JobEntryJobDialog.Exception.UnableToReferenceObjectId.Title"),
                                BaseMessages
                                        .getString(PKG,
                                                "JobEntryJobDialog.Exception.UnableToReferenceObjectId.Message"),
                                e);
                    }
                }
                break;
        }
        setRadioButtons();

        // Arguments
        if (jobEntry.arguments != null)
        {
            for (int i = 0; i < jobEntry.arguments.length; i++)
            {
                TableItem ti = wFields.table.getItem(i);
                if (jobEntry.arguments[i] != null)
                    ti.setText(1, jobEntry.arguments[i]);
            }
            wFields.setRowNums();
            wFields.optWidth(true);
        }

        // Parameters
        if (jobEntry.parameters != null)
        {
            for (int i = 0; i < jobEntry.parameters.length; i++)
            {
                TableItem ti = wParameters.table.getItem(i);
                if (!Const.isEmpty(jobEntry.parameters[i]))
                {
                    ti.setText(1, Const.NVL(jobEntry.parameters[i], ""));
                    ti.setText(2,
                            Const.NVL(jobEntry.parameterFieldNames[i], ""));
                    ti.setText(3, Const.NVL(jobEntry.parameterValues[i], ""));
                }
            }
            wParameters.setRowNums();
            wParameters.optWidth(true);
        }

        wPassParams.setSelection(jobEntry.isPassingAllParameters());

        wPrevious.setSelection(jobEntry.argFromPrevious);
        wPrevToParams.setSelection(jobEntry.paramsFromPrevious);
        wSetLogfile.setSelection(jobEntry.setLogfile);
        if (jobEntry.logfile != null)
            wLogfile.setText(jobEntry.logfile);
        if (jobEntry.logext != null)
            wLogext.setText(jobEntry.logext);
        wAddDate.setSelection(jobEntry.addDate);
        wAddTime.setSelection(jobEntry.addTime);

        if (jobEntry.getRemoteSlaveServerName() != null)
        {
            wSlaveServer.setText(jobEntry.getRemoteSlaveServerName());
        }
        wPassExport.setSelection(jobEntry.isPassingExport());

        if (jobEntry.logFileLevel != null)
        {
            wLoglevel.select(jobEntry.logFileLevel.getLevel());
        }
        else
        {
            // Set the default log level
            wLoglevel.select(JobEntryProvenanceCollector.DEFAULT_LOG_LEVEL
                    .getLevel());
        }
        wAppendLogfile.setSelection(jobEntry.setAppendLogfile);
        wCreateParentFolder.setSelection(jobEntry.createParentFolder);
        wWaitingToFinish.setSelection(jobEntry.isWaitingToFinish());
        wFollowingAbortRemotely.setSelection(jobEntry
                .isFollowingAbortRemotely());

        // Rogers (05/2013): Provenance Tab
        // 1- Provenance Connection
        if (jobEntry.getProvConnection() != null)
        {
            wProvCmbConnection.setText(jobEntry.getProvConnection().getName());
        }

        // 2- Fine-grained Steps
        if (jobEntry.getMapFineGrainedEnabled() != null)
        {
            Map<String, FineGrainedStep> fgStepMap = FineGrainedStepMap.get()
                    .getFineGrainedStepMap();
            Map<FineGrainedStep, Boolean> fgEnabledMap = jobEntry
                    .getMapFineGrainedEnabled();
            for (TableItem item : wProvTblFGSteps.table.getItems())
            {
                String stepId = item.getText(COLUMN_STEP_ID);
                FineGrainedStep fgStep = fgStepMap.get(stepId);
                item.setChecked(fgEnabledMap.get(fgStep));
            }
        }
        selectGroupEnabledFGStep(wProvTblFGSteps.table);
    }

    private void cancel()
    {
        jobEntry.setChanged(backupChanged);

        jobEntry = null;
        dispose();
    }

    private void getInfo(JobEntryProvenanceCollector jej)
    {
        jej.setName(wName.getText());
        jej.setSpecificationMethod(specificationMethod);
        switch (specificationMethod)
        {
            case FILENAME:
                jej.setFileName(wFilename.getText());
                jej.setDirectory(null);
                jej.setJobName(null);
                jej.setJobObjectId(null);
                break;
            case REPOSITORY_BY_NAME:
                jej.setFileName(null);
                jej.setDirectory(wDirectory.getText());
                jej.setJobName(wJobname.getText());
                jej.setJobObjectId(null);
                break;
            case REPOSITORY_BY_REFERENCE:
                jej.setFileName(null);
                jej.setDirectory(null);
                jej.setJobName(null);
                jej.setJobObjectId(referenceObjectId);
                break;
        }

        // Do the arguments
        int nritems = wFields.nrNonEmpty();
        int nr = 0;
        for (int i = 0; i < nritems; i++)
        {
            String arg = wFields.getNonEmpty(i).getText(1);
            if (arg != null && arg.length() != 0)
                nr++;
        }
        jej.arguments = new String[nr];
        nr = 0;
        for (int i = 0; i < nritems; i++)
        {
            String arg = wFields.getNonEmpty(i).getText(1);
            if (arg != null && arg.length() != 0)
            {
                jej.arguments[nr] = arg;
                nr++;
            }
        }

        // Do the parameters
        nritems = wParameters.nrNonEmpty();
        nr = 0;
        for (int i = 0; i < nritems; i++)
        {
            String param = wParameters.getNonEmpty(i).getText(1);
            if (param != null && param.length() != 0)
                nr++;
        }
        jej.parameters = new String[nr];
        jej.parameterFieldNames = new String[nr];
        jej.parameterValues = new String[nr];
        nr = 0;
        for (int i = 0; i < nritems; i++)
        {
            String param = wParameters.getNonEmpty(i).getText(1);
            String fieldName = wParameters.getNonEmpty(i).getText(2);
            String value = wParameters.getNonEmpty(i).getText(3);

            jej.parameters[nr] = param;

            if (!Const.isEmpty(Const.trim(fieldName)))
            {
                jej.parameterFieldNames[nr] = fieldName;
            }
            else
            {
                jej.parameterFieldNames[nr] = "";
            }

            if (!Const.isEmpty(Const.trim(value)))
            {
                jej.parameterValues[nr] = value;
            }
            else
            {
                jej.parameterValues[nr] = "";
            }

            nr++;
        }
        jej.setPassingAllParameters(wPassParams.getSelection());

        jej.setLogfile = wSetLogfile.getSelection();
        jej.addDate = wAddDate.getSelection();
        jej.addTime = wAddTime.getSelection();
        jej.logfile = wLogfile.getText();
        jej.logext = wLogext.getText();
        if (wLoglevel.getSelectionIndex() >= 0)
        {
            jej.logFileLevel = LogLevel.values()[wLoglevel.getSelectionIndex()];
        }
        else
        {
            jej.logFileLevel = LogLevel.BASIC;
        }
        jej.argFromPrevious = wPrevious.getSelection();
        jej.paramsFromPrevious = wPrevToParams.getSelection();
        jej.execPerRow = wEveryRow.getSelection();

        jej.setRemoteSlaveServerName(wSlaveServer.getText());
        jej.setPassingExport(wPassExport.getSelection());
        jej.setAppendLogfile = wAppendLogfile.getSelection();
        jej.setWaitingToFinish(wWaitingToFinish.getSelection());
        jej.createParentFolder = wCreateParentFolder.getSelection();
        jej.setFollowingAbortRemotely(wFollowingAbortRemotely.getSelection());

        // Rogers (05/2013): Provenance Tab
        // 1- Set Provenance Connection
        jobEntry.setProvConnection(jobMeta.findDatabase(wProvCmbConnection
                .getText()));

        // 2- Set Fine-Grained Steps Map
        nritems = wProvTblFGSteps.table.getItemCount();
        jobEntry.getMapFineGrainedEnabled().clear();
        Map<String, FineGrainedStep> fgMap = FineGrainedStepMap.get()
                .getFineGrainedStepMap();
        for (int i = 0; i < nritems; i++)
        {
            String type = wProvTblFGSteps.table.getItem(i).getText(1);
            boolean enabled = wProvTblFGSteps.table.getItem(i).getChecked();
            jobEntry.getMapFineGrainedEnabled().put(fgMap.get(type), enabled);
        }
    }

    private void ok()
    {
        if (Const.isEmpty(wName.getText()))
        {
            MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
            mb.setText(BaseMessages.getString(PKG,
                    "System.StepJobEntryNameMissing.Title"));
            mb.setMessage(BaseMessages.getString(PKG,
                    "System.JobEntryNameMissing.Msg"));
            mb.open();
            return;
        }

        if (Const.isEmpty(wProvCmbConnection.getText()))
        {
            MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING);
            mb.setText(BaseMessages.getString(PKG,
                    "JobJob.Provenance.Warning.Box.Title"));
            mb.setMessage(BaseMessages.getString(PKG,
                    "JobJob.Provenance.Warning.Box.Msg"));
            mb.open();
        }
        getInfo(jobEntry);
        dispose();
    }
}