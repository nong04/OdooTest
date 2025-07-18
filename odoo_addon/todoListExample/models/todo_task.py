from odoo import models,fields

class TodoTask(models.Model):
    _name = 'todo.task'
    _description = 'Todo Task'

    name = fields.Char('Tên Task', required = True)
    description = fields.Char("Ghi chú", required = True)
    due_date = fields.Date(string = '')
    done = fields.Boolean('Đã Hoàn Thành', default = False)