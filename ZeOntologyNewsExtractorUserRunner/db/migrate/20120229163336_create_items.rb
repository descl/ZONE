class CreateItems < ActiveRecord::Migration
  def change
    create_table :items do |t|
      t.string :typeItem
      t.string :value
      t.integer :inc

      t.timestamps
    end
  end
end
