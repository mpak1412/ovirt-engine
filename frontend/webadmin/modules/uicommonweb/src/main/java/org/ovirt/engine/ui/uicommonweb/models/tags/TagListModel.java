package org.ovirt.engine.ui.uicommonweb.models.tags;
import java.util.Collections;
import org.ovirt.engine.core.compat.*;
import org.ovirt.engine.ui.uicompat.*;
import org.ovirt.engine.core.common.businessentities.*;
import org.ovirt.engine.core.common.vdscommands.*;
import org.ovirt.engine.core.common.queries.*;
import org.ovirt.engine.core.common.action.*;
import org.ovirt.engine.ui.frontend.*;
import org.ovirt.engine.ui.uicommonweb.*;
import org.ovirt.engine.ui.uicommonweb.models.*;
import org.ovirt.engine.core.common.*;

import org.ovirt.engine.ui.uicommonweb.dataprovider.*;
import org.ovirt.engine.ui.uicommonweb.models.common.*;
import org.ovirt.engine.ui.uicompat.*;
import org.ovirt.engine.core.common.businessentities.*;

import org.ovirt.engine.core.common.queries.*;
import org.ovirt.engine.ui.uicommonweb.*;
import org.ovirt.engine.ui.uicommonweb.models.*;

@SuppressWarnings("unused")
public class TagListModel extends SearchableListModel
{

	public static EventDefinition ResetRequestedEventDefinition;
	private Event privateResetRequestedEvent;
	public Event getResetRequestedEvent()
	{
		return privateResetRequestedEvent;
	}
	private void setResetRequestedEvent(Event value)
	{
		privateResetRequestedEvent = value;
	}



	private UICommand privateNewCommand;
	public UICommand getNewCommand()
	{
		return privateNewCommand;
	}
	private void setNewCommand(UICommand value)
	{
		privateNewCommand = value;
	}
	private UICommand privateEditCommand;
	public UICommand getEditCommand()
	{
		return privateEditCommand;
	}
	private void setEditCommand(UICommand value)
	{
		privateEditCommand = value;
	}
	private UICommand privateRemoveCommand;
	public UICommand getRemoveCommand()
	{
		return privateRemoveCommand;
	}
	private void setRemoveCommand(UICommand value)
	{
		privateRemoveCommand = value;
	}
	private UICommand privateResetCommand;
	public UICommand getResetCommand()
	{
		return privateResetCommand;
	}
	private void setResetCommand(UICommand value)
	{
		privateResetCommand = value;
	}



	public TagModel getSelectedItem()
	{
		return (TagModel)super.getSelectedItem();
	}
	public void setSelectedItem(TagModel value)
	{
		super.setSelectedItem(value);
	}

	public Iterable getItems()
	{
		return items;
	}
	public void setItems(Iterable value)
	{
		if (items != value)
		{
			ItemsChanging(value, items);
			items = value;
			ItemsChanged();
			getItemsChangedEvent().raise(this, EventArgs.Empty);
			OnPropertyChanged(new PropertyChangedEventArgs("Items"));
		}
	}

	private java.util.ArrayList<SelectionTreeNodeModel> selectionNodeList;
	public java.util.ArrayList<SelectionTreeNodeModel> getSelectionNodeList()
	{
		return selectionNodeList;
	}
	public void setSelectionNodeList(java.util.ArrayList<SelectionTreeNodeModel> value)
	{
		if (selectionNodeList != value)
		{
			selectionNodeList = value;
			OnPropertyChanged(new PropertyChangedEventArgs("SelectionNodeList"));
		}
	}

	private java.util.Map<Guid, Boolean> attachedTagsToEntities;
	public java.util.Map<Guid, Boolean> getAttachedTagsToEntities()
	{
		return attachedTagsToEntities;
	}
	public void setAttachedTagsToEntities(java.util.Map<Guid, Boolean> value)
	{
		if (attachedTagsToEntities != value)
		{
			attachedTagsToEntities = value;
			AttachedTagsToEntitiesChanged();
			OnPropertyChanged(new PropertyChangedEventArgs("AttachedTagsToEntities"));
		}
	}


	static
	{
		ResetRequestedEventDefinition = new EventDefinition("ResetRequested", SystemTreeModel.class);
	}

	public TagListModel()
	{
		setResetRequestedEvent(new Event(ResetRequestedEventDefinition));

		setNewCommand(new UICommand("New", this));
		setEditCommand(new UICommand("Edit", this));
		setRemoveCommand(new UICommand("Remove", this));
		setResetCommand(new UICommand("Reset", this));

		setIsTimerDisabled(true);

		getSearchCommand().Execute();

		UpdateActionAvailability();

		//Initialize SelectedItems property with empty collection.
		setSelectedItems(new java.util.ArrayList<TagModel>());

		setSelectionNodeList(new java.util.ArrayList<SelectionTreeNodeModel>());
	}

	@Override
	protected void SyncSearch()
	{
		super.SyncSearch();

		AsyncDataProvider.GetRootTag(new AsyncQuery(this,
		new INewAsyncCallback() {
			@Override
			public void OnSuccess(Object target, Object returnValue) {

			TagListModel tagListModel = (TagListModel)target;
			TagModel rootTag = tagListModel.TagToModel((org.ovirt.engine.core.common.businessentities.tags)returnValue);
			rootTag.getName().setEntity("Root");
			rootTag.setType(TagModelType.Root);
			rootTag.setIsChangable(false);
			tagListModel.setItems(new java.util.ArrayList<TagModel>(java.util.Arrays.asList(new TagModel[] { rootTag })));

			}
		}));
	}

	@Override
	protected void ItemsChanged()
	{
		super.ItemsChanged();

		if (getSelectionNodeList().isEmpty() && getAttachedTagsToEntities() != null)
		{
			AttachedTagsToEntitiesChanged();
		}
	}

	protected void AttachedTagsToEntitiesChanged()
	{
		java.util.ArrayList<TagModel> tags = (java.util.ArrayList<TagModel>)getItems();

		if (tags != null)
		{
			TagModel root = tags.get(0);

			if (getAttachedTagsToEntities() != null)
			{
				RecursiveSetSelection(root, getAttachedTagsToEntities());
			}

			if (getSelectionNodeList().isEmpty())
			{
				setSelectionNodeList(new java.util.ArrayList<SelectionTreeNodeModel>(java.util.Arrays.asList(new SelectionTreeNodeModel[] {CreateTree(root)})));
			}
		}
	}

	public void RecursiveSetSelection(TagModel tagModel, java.util.Map<Guid, Boolean> attachedEntities)
	{
		if (attachedEntities.containsKey(tagModel.getId()) && attachedEntities.get(tagModel.getId()))
		{
			tagModel.setSelection(true);
		}
		else
		{
			tagModel.setSelection(false);
		}
		if (tagModel.getChildren() != null)
		{
			for (TagModel subModel : tagModel.getChildren())
			{
				RecursiveSetSelection(subModel, attachedEntities);
			}
		}
	}

	public SelectionTreeNodeModel CreateTree(TagModel tag)
	{
		SelectionTreeNodeModel node = new SelectionTreeNodeModel();
		node.setDescription(tag.getName().getEntity().toString());
		node.setIsSelectedNullable(tag.getSelection());
		node.setIsChangable(tag.getIsChangable());
		node.setIsSelectedNotificationPrevent(true);
		node.setEntity(tag);
		node.getPropertyChangedEvent().addListener(this);

		if (tag.getChildren().isEmpty())
		{
			getSelectionNodeList().add(node);
			return node;
		}

		for (TagModel childTag : tag.getChildren())
		{
			SelectionTreeNodeModel childNode = CreateTree(childTag);
			childNode.setParent(node);
			node.getChildren().add(childNode);
		}

		return node;
	}

	public TagModel CloneTagModel(TagModel tag)
	{
		java.util.ArrayList<TagModel> children = new java.util.ArrayList<TagModel>();
		for (TagModel child : tag.getChildren())
		{
			children.add(CloneTagModel(child));
		}

		TagModel model = new TagModel();
		model.setId(tag.getId());
		model.setName(tag.getName());
		model.setDescription(tag.getDescription());
		model.setType(tag.getType());
		model.setSelection(tag.getSelection());
		model.setParentId(tag.getParentId());
		model.setChildren(children);
		model.getSelectionChangedEvent().addListener(this);

		return model;
	}

	public TagModel TagToModel(org.ovirt.engine.core.common.businessentities.tags tag)
	{
		EntityModel tempVar = new EntityModel();
		tempVar.setEntity(tag.gettag_name());
		EntityModel name = tempVar;
		EntityModel tempVar2 = new EntityModel();
		tempVar2.setEntity(tag.getdescription());
		EntityModel description = tempVar2;

		java.util.ArrayList<TagModel> children = new java.util.ArrayList<TagModel>();
		for (org.ovirt.engine.core.common.businessentities.tags a : tag.getChildren())
		{
			children.add(TagToModel(a));
		}

		TagModel model = new TagModel();
		model.setId(tag.gettag_id());
		model.setName(name);
		model.setDescription(description);
		model.setType((tag.getIsReadonly() == null ? false : tag.getIsReadonly()) ? TagModelType.ReadOnly : TagModelType.Regular);
		model.setSelection(false);
		model.setParentId(tag.getparent_id() == null ? Guid.Empty : tag.getparent_id().getValue());
		model.setChildren(children);

		model.getSelectionChangedEvent().addListener(this);

		return model;
	}

	@Override
	public void eventRaised(Event ev, Object sender, EventArgs args)
	{
		super.eventRaised(ev, sender, args);

		if (ev.equals(TagModel.SelectionChangedEventDefinition))
		{
			OnTagSelectionChanged(sender, args);
		}
	}

	@Override
	protected void EntityPropertyChanged(Object sender, PropertyChangedEventArgs e)
	{
		super.EntityPropertyChanged(sender, e);
		if (e.PropertyName.equals("IsSelectedNullable"))
		{
			SelectionTreeNodeModel selectionTreeNodeModel = (SelectionTreeNodeModel) sender;
			TagModel tagModel = (TagModel)selectionTreeNodeModel.getEntity();

			tagModel.setSelection(selectionTreeNodeModel.getIsSelectedNullable());
			OnTagSelectionChanged(tagModel, e);
		}
	}

	private void OnTagSelectionChanged(Object sender, EventArgs e)
	{
		TagModel model = (TagModel)sender;

		java.util.ArrayList<TagModel> list = new java.util.ArrayList<TagModel>();
		if (getSelectedItems() != null)
		{
			for (Object item : getSelectedItems())
			{
				list.add((TagModel)item);
			}
		}

		if ((model.getSelection() == null ? false : model.getSelection()))
		{
			list.add(model);
		}
		else
		{
			list.remove(model);
		}

		setSelectedItems(list);
	}

	@Override
	protected void AsyncSearch()
	{
		super.AsyncSearch();
		SyncSearch();
	}

	private void Reset()
	{
		setSelectedItems(new java.util.ArrayList<TagModel>());

		if (getItems() != null)
		{
			for (Object item : getItems())
			{
				ResetInternal((TagModel) item);
			}
		}

		// Async tag search will cause tree selection to be cleared
		// Search();

		getResetRequestedEvent().raise(this, EventArgs.Empty);
	}

	private void ResetInternal(TagModel root)
	{
		root.setSelection(false);
		for (TagModel item : root.getChildren())
		{
			ResetInternal(item);
		}
	}

	public void remove()
	{
		if (getWindow() != null)
		{
			return;
		}

		ConfirmationModel model = new ConfirmationModel();
		setWindow(model);
		model.setTitle("Remove Tag(s)");
		model.setHashName("remove_tag");
		model.setMessage("Tag(s):");

		java.util.ArrayList<Object> items = new java.util.ArrayList<Object>();
		items.add(getSelectedItem().getName().getEntity());
		model.setItems(items);

		model.setNote("NOTE:\n  - Removing the tag will also remove all of its descendants.\n  - Tag and descendants will be erased from all objects that are attached to them.");

		UICommand tempVar = new UICommand("OnRemove", this);
		tempVar.setTitle("OK");
		tempVar.setIsDefault(true);
		model.getCommands().add(tempVar);
		UICommand tempVar2 = new UICommand("Cancel", this);
		tempVar2.setTitle("Cancel");
		tempVar2.setIsCancel(true);
		model.getCommands().add(tempVar2);
	}

	public void OnRemove()
	{
		ConfirmationModel model = (ConfirmationModel)getWindow();

		if (model.getProgress() != null)
		{
			return;
		}

		model.StartProgress(null);

		Frontend.RunAction(VdcActionType.RemoveTag, new TagsActionParametersBase(getSelectedItem().getId()),
		new IFrontendActionAsyncCallback() {
			@Override
			public void Executed(FrontendActionAsyncResult  result) {

			TagListModel tagListModel = (TagListModel)result.getState();
			VdcReturnValueBase returnVal = result.getReturnValue();
			boolean success = returnVal != null && returnVal.getSucceeded();
			if (success)
			{
				tagListModel.getSearchCommand().Execute();
			}
			tagListModel.Cancel();
			tagListModel.StopProgress();

			}
		}, this);
	}

	public void Edit()
	{
		if (getWindow() != null)
		{
			return;
		}

		TagModel model = new TagModel();
		setWindow(model);
		model.setTitle("Edit Tag");
		model.setHashName("edit_tag");
		model.setIsNew(false);
		model.getName().setEntity(getSelectedItem().getName().getEntity());
		model.getDescription().setEntity(getSelectedItem().getDescription().getEntity());
		model.setParentId(getSelectedItem().getParentId());

		UICommand tempVar = new UICommand("OnSave", this);
		tempVar.setTitle("OK");
		tempVar.setIsDefault(true);
		model.getCommands().add(tempVar);
		UICommand tempVar2 = new UICommand("Cancel", this);
		tempVar2.setTitle("Cancel");
		tempVar2.setIsCancel(true);
		model.getCommands().add(tempVar2);
	}

	public void New()
	{
		if (getWindow() != null)
		{
			return;
		}

		TagModel model = new TagModel();
		setWindow(model);
		model.setTitle("New Tag");
		model.setHashName("new_tag");
		model.setIsNew(true);

		UICommand tempVar = new UICommand("OnSave", this);
		tempVar.setTitle("OK");
		tempVar.setIsDefault(true);
		model.getCommands().add(tempVar);
		UICommand tempVar2 = new UICommand("Cancel", this);
		tempVar2.setTitle("Cancel");
		tempVar2.setIsCancel(true);
		model.getCommands().add(tempVar2);
	}

	public void OnSave()
	{
		TagModel model = (TagModel)getWindow();

		if (model.getProgress() != null)
		{
			return;
		}

		if (!model.Validate())
		{
			return;
		}

		org.ovirt.engine.core.common.businessentities.tags tempVar = new org.ovirt.engine.core.common.businessentities.tags();
		tempVar.settag_id(model.getIsNew() ? Guid.Empty : getSelectedItem().getId());
		tempVar.setparent_id(model.getIsNew() ? getSelectedItem().getId() : getSelectedItem().getParentId());
		tempVar.settag_name((String)model.getName().getEntity());
		tempVar.setdescription((String)model.getDescription().getEntity());
		org.ovirt.engine.core.common.businessentities.tags tag = tempVar;


		model.StartProgress(null);

		Frontend.RunAction(model.getIsNew() ? VdcActionType.AddTag : VdcActionType.UpdateTag, new TagsOperationParameters(tag),
		new IFrontendActionAsyncCallback() {
			@Override
			public void Executed(FrontendActionAsyncResult  result) {

			TagListModel localModel = (TagListModel)result.getState();
			localModel.PostOnSave(result.getReturnValue());

			}
		}, this);
	}

	public void PostOnSave(VdcReturnValueBase returnValue)
	{
		TagModel model = (TagModel)getWindow();

		model.StopProgress();

		if (returnValue != null && returnValue.getSucceeded())
		{
			Cancel();
			getSearchCommand().Execute();
		}
	}

	public void Cancel()
	{
		setWindow(null);
	}

	@Override
	protected void OnSelectedItemChanged()
	{
		super.OnSelectedItemChanged();
		UpdateActionAvailability();
	}

	private void UpdateActionAvailability()
	{
		getNewCommand().setIsExecutionAllowed(getSelectedItem() != null);
		getEditCommand().setIsExecutionAllowed(getSelectedItem() != null && getSelectedItem().getType() == TagModelType.Regular);
		getRemoveCommand().setIsExecutionAllowed(getSelectedItem() != null && getSelectedItem().getType() == TagModelType.Regular);
	}

	@Override
	public void ExecuteCommand(UICommand command)
	{
		super.ExecuteCommand(command);

		if (command == getResetCommand())
		{
			Reset();
		}
		else if (command == getNewCommand())
		{
			New();
		}
		else if (command == getEditCommand())
		{
			Edit();
		}
		else if (command == getRemoveCommand())
		{
			remove();
		}
		else if (StringHelper.stringsEqual(command.getName(), "Cancel"))
		{
			Cancel();
		}
		else if (StringHelper.stringsEqual(command.getName(), "OnSave"))
		{
			OnSave();
		}
		else if (StringHelper.stringsEqual(command.getName(), "OnRemove"))
		{
			OnRemove();
		}
	}
    @Override
    protected String getListName() {
        return "TagListModel";
    }
}