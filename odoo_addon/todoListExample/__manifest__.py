{
    'name': 'Todo List Custom',
    'version': '1.0',
    'summary': 'Simple To-Do List Module',
    'author': 'Do Thanh Long',
    'category': 'Productivity',
    'depends': ['base'],
    'data': [
        'security/ir.model.access.csv',
        'views/todo_task_views.xml',
    ],
    'installable': True,
    'application': True,
    'license': 'LGPL-3',
}