const roles = ['ADMIN', 'READ-ONLY', 'CUSTOMER'];

const alphaNumPattern = {
    value: /^[a-zA-Z0-9]+$/,
    message: 'Only alphabets and numbers are allowed',
};

const passwordPattern = {
    value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/,
    message:
        'Password must include uppercase, lowercase, number, and special character',

};

const usernamePattern = {
    value: /^[a-zA-Z0-9_@-]+$/,
    message: 'Only letters, numbers, underscores (_), hyphens (-), and @ are allowed in username',
};



const FormConfig = (isEditMode) => [
    {
        name: 'firstName',
        label: 'First Name',
        type: 'text',
        rules: isEditMode
            ? {}
            : { required: 'First Name is required', pattern: alphaNumPattern },
    },
    {
        name: 'lastName',
        label: 'Last Name',
        type: 'text',
        rules: isEditMode
            ? {}
            : { required: 'Last Name is required', pattern: alphaNumPattern },
    },
    {
        name: 'email',
        label: 'Email',
        type: 'email',
        rules: isEditMode
            ? {}
            : {
                required: 'Email is required',
                pattern: {
                    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                    message: 'Invalid email format',
                },
            },
    },
    {
        name: 'username',
        label: 'Username',
        type: 'text',
        rules: isEditMode
            ? {}
            : {
                required: 'Username is required',
                pattern: usernamePattern,
                minLength: {
                    value: 6,
                    message: 'Username must be at least 6 characters',
                },
            },
    },
    {
        name: 'password',
        label: 'Password',
        type: 'password',
        rules: isEditMode
            ? {}
            : {
                required: 'Password is required',
                minLength: {
                    value: 8,
                    message: 'Password must be at least 8 characters',
                },
                pattern: passwordPattern
            },
    },
    {
        name: 'role',
        label: 'Select Role',
        type: 'select',
        options: roles,
        rules: { required: 'Role is required' },
    },
];

export default FormConfig;
