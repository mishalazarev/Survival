package white.ball.survival.present.dialog_screen.backpack.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import white.ball.survival.R
import white.ball.survival.databinding.ItemSlotBinding
import white.ball.survival.domain.model.build.Hut
import white.ball.survival.domain.model.build.StoneHouse
import white.ball.survival.domain.model.build.WoodHouse
import white.ball.survival.domain.model.extension_model.ItemUse
import white.ball.survival.domain.model.extension_model.ItemsSlot
import white.ball.survival.domain.repository.BuildRepository
import white.ball.survival.domain.repository.ItemUseRepository
import white.ball.survival.domain.repository.Plant

class BackpackAdapter(
    private val itemUseRepository: ItemUseRepository,
    private val context: Context,
    private val build: BuildRepository?,
) : RecyclerView.Adapter<BackpackAdapter.ItemHolder>(), View.OnLongClickListener {

    lateinit var menu: PopupMenu
    var mItemsSlotList: List<ItemsSlot> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class ItemHolder(val binding: ItemSlotBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: ItemsSlot) = with(binding) {
            wrapperItemConstraintLayout.tag = items
            countItemTextView.text = "${items.count} шт."
            itemImageView.setImageResource(items.item.imageId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val binding = ItemSlotBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.wrapperItemConstraintLayout.setOnLongClickListener(this)

        return ItemHolder(binding)
    }

    override fun getItemCount(): Int = mItemsSlotList.size

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = mItemsSlotList[position]
        holder.bind(item)
    }

    override fun onLongClick(view: View?): Boolean {
        val items: ItemsSlot = view!!.tag as ItemsSlot
        menu = PopupMenu(context, view)
        menu.inflate(R.menu.menu_item_use)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.build_item -> {
                    itemUseRepository.setBuild(items)
                    true
                }

                R.id.grow_plant -> {
                    itemUseRepository.growPlant(items)
                    true
                }

                R.id.use_item -> {
                    itemUseRepository.takeFood(items)
                    true
                }

                R.id.join_similar_items -> {
                    itemUseRepository.joinSimilarItems(items)
                    true
                }

                R.id.shell_item_open -> {
                    itemUseRepository.openShell(items)
                    true
                }

                R.id.scroll_item_open -> {
                    itemUseRepository.openScroll(items)
                    true
                }

                R.id.build_item -> {
                    itemUseRepository.setBuild(items)
                    true
                }

                R.id.put_anti_thief -> {
                    itemUseRepository.setAntiThief(items)
                    true
                }

                R.id.item_put -> {
                    itemUseRepository.setPutOnItem(items)
                    true
                }

                R.id.throw_into_cook -> {
                    itemUseRepository.setIntoCook(items)
                    true
                }

                R.id.about_item -> {
                    itemUseRepository.findOutInformation(items)
                    true
                }

                R.id.divide_into_quality_groups -> {
                    itemUseRepository.divideIntoQualityGroups(items)
                    true
                }

                R.id.threw_it_away_item -> {
                    itemUseRepository.threwItAway(items)
                    true
                }

                R.id.threw_it_away_all_item -> {
                    itemUseRepository.threwAllItemsAway(items)
                    true
                }

                else -> false
            }
        }

        loadItemMenuChoice(menu, items)

        menu.show()
        return true
    }

    fun loadItemMenuChoice(menuWithItem: PopupMenu, items: ItemsSlot) {
        var countRepeatItemsSlot = 0

        if (items.count == 1) {
            menuWithItem.menu.removeItem(R.id.divide_into_quality_groups)
            menuWithItem.menu.removeItem(R.id.threw_it_away_all_item)
        }

        if (build == null) {
            menuWithItem.menu.removeItem(R.id.throw_into_cook)
            menuWithItem.menu.removeItem(R.id.put_anti_thief)
            menuWithItem.menu.removeItem(R.id.grow_plant)
        }
        // add other builds


        mItemsSlotList.forEach {
            if (it.item.nameItem == items.item.nameItem) countRepeatItemsSlot++
        }

        when (items.item.itemUse) {

            ItemUse.SEED_IN_SHELL -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.OPENED_SEED -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.put_anti_thief)

                    when (build) {
                        is StoneHouse, is WoodHouse, is Hut -> {}
                        else -> {
                            menu.removeItem(R.id.grow_plant)
                        }
                    }
                }
            }

            ItemUse.BUILD -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.ARROW -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.ARMOR, ItemUse.WEAPON, ItemUse.BOW -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.FOR_COOK -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.ANTI_THIEF -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.item_put)

                    if (build == null) {
                        menu.removeItem(R.id.put_anti_thief)
                    }
                }
            }

            ItemUse.SCROLL -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.OPENED_SCROLL -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.FOOD -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.DRINK -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.FOR_FIRE -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }

            ItemUse.INFO -> {
                with(menuWithItem) {
                    menu.removeItem(R.id.grow_plant)
                    menu.removeItem(R.id.shell_item_open)
                    menu.removeItem(R.id.build_item)
                    menu.removeItem(R.id.scroll_item_open)
                    menu.removeItem(R.id.use_item)
                    menu.removeItem(R.id.item_put)
                    menu.removeItem(R.id.throw_into_cook)
                    menu.removeItem(R.id.put_anti_thief)
                }
            }
        }
    }
}
