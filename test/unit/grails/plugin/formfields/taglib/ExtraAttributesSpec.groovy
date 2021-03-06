package grails.plugin.formfields.taglib

import grails.plugin.formfields.mock.Person
import spock.lang.Issue
import grails.plugin.formfields.*
import grails.test.mixin.*

@Issue('https://github.com/grails-fields-plugin/grails-fields/pull/17')
@TestFor(FormFieldsTagLib)
@Mock(Person)
class ExtraAttributesSpec extends AbstractFormFieldsTagLibSpec {

	def mockFormFieldsTemplateService = Mock(FormFieldsTemplateService)

	def setupSpec() {
		configurePropertyAccessorSpringBean()
	}

	def setup() {
		def taglib = applicationContext.getBean(FormFieldsTagLib)

        views["/_fields/_layouts/_noLayout.gsp"] = '${raw(renderedField)}'
        mockFormFieldsTemplateService.findTemplate(_, 'field', null) >> [path: '/_fields/default/field']
		taglib.formFieldsTemplateService = mockFormFieldsTemplateService
	}

    void 'arbitrary attributes can be passed to the field template model for backward compatibility'() {
        given:
        views["/_fields/default/_field.gsp"] = '${foo}'

        expect:
        applyTemplate('<f:field bean="personInstance" property="name" foo="bar"/>', [personInstance: personInstance]) == 'bar'
    }

    void 'arbitrary attributes are be passed to the field template model in "attrs"'() {
        given:
        views["/_fields/default/_field.gsp"] = '${attrs.foo}'

        expect:
        applyTemplate('<f:field bean="personInstance" property="name" foo="bar"/>', [personInstance: personInstance]) == 'bar'
    }

    void 'arbitrary attributes on f:field are not passed to the input template'() {
        given:
        views["/_fields/default/_field.gsp"] = '${widget}'
        views["/_fields/person/name/_input.gsp"] = '<span>${foo}${attrs?.foo}</span>'

		and:
		mockFormFieldsTemplateService.findTemplate(_, 'input', null) >> [path: '/_fields/person/name/input']

        expect:
        applyTemplate('<f:field bean="personInstance" property="name" foo="bar"/>', [personInstance: personInstance]) == '<span></span>'
    }

    void 'arbitrary attributes on f:field are not passed to the default input'() {
        given:
        views["/_fields/default/_field.gsp"] = '${widget}'

        expect:
        applyTemplate('<f:field bean="personInstance" property="name" foo="bar"/>', [personInstance: personInstance]) == '<input type="text" name="name" value="Bart Simpson" required="" id="name" />'
    }

    void 'arbitrary attributes can be passed to the display template model for backward compatibility'() {
        given:
        views["/_fields/default/_display.gsp"] = '${foo}'

        and:
        mockFormFieldsTemplateService.findTemplate(_, 'display', null) >> [path: '/_fields/default/display']

        expect:
        applyTemplate('<f:display bean="personInstance" property="name" foo="bar"/>', [personInstance: personInstance]) == 'bar'
    }

    void 'arbitrary attributes are be passed to the display template model in "attrs"'() {
        given:
        views["/_fields/default/_display.gsp"] = '${attrs.foo}'

        and:
        mockFormFieldsTemplateService.findTemplate(_, 'display', null) >> [path: '/_fields/default/display']

        expect:
        applyTemplate('<f:display bean="personInstance" property="name" foo="bar"/>', [personInstance: personInstance]) == 'bar'
    }

	void 'arbitrary attributes on f:input are passed to the input template'() {
		given:
		views["/_fields/person/name/_input.gsp"] = '${foo}'

		and:
		mockFormFieldsTemplateService.findTemplate(_, 'input', null) >> [path: '/_fields/person/name/input']

		expect:
		applyTemplate('<f:input bean="personInstance" property="name" foo="bar"/>', [personInstance: personInstance]) == 'bar'
	}

	void 'arbitrary attributes on f:input are passed to the default input'() {
		expect:
		applyTemplate('<f:input bean="personInstance" property="name" foo="bar"/>', [personInstance: personInstance]) == '<input type="text" foo="bar" name="name" value="Bart Simpson" required="" id="name" />'
	}

	@Issue("https://github.com/grails-fields-plugin/grails-fields/pull/49")
    void 'arbitrary attributes prefixed with input- are not passed to the field template'() {
        given:
        views["/_fields/default/_field.gsp"] = '<foo>${foo}</foo>'

        expect:
        applyTemplate('<f:field bean="personInstance" property="name" input-foo="bar" />', [personInstance: personInstance]) == '<foo></foo>'
    }

    void 'arbitrary attributes prefixed with input- on f:field are added to the input template model for backward compatibility'() {
        given:
        views["/_fields/default/_field.gsp"] = '${widget}'
        views["/_fields/person/name/_input.gsp"] = '<span>${foo}</span>'

		and:
		mockFormFieldsTemplateService.findTemplate(_, 'input', null) >> [path: '/_fields/person/name/input']

        expect:
        applyTemplate('<f:field bean="personInstance" property="name" input-foo="bar"/>', [personInstance: personInstance]) == '<span>bar</span>'
    }

    void 'arbitrary attributes prefixed with input- on f:field are passed to the input template as attrs'() {
        given:
        views["/_fields/default/_field.gsp"] = '${widget}'
        views["/_fields/person/name/_input.gsp"] = '<span>${attrs.foo}</span>'

		and:
		mockFormFieldsTemplateService.findTemplate(_, 'input', null) >> [path: '/_fields/person/name/input']

        expect:
        applyTemplate('<f:field bean="personInstance" property="name" input-foo="bar"/>', [personInstance: personInstance]) == '<span>bar</span>'
    }

	@Issue("https://github.com/grails-fields-plugin/grails-fields/pull/49")
    void 'arbitrary attributes prefixed with input- on f:field are passed to the default input'() {
        given:
        views["/_fields/default/_field.gsp"] = '${widget}'

        expect:
        applyTemplate('<f:field bean="personInstance" property="name" input-foo="bar"/>', [personInstance: personInstance]) == '<input type="text" foo="bar" name="name" value="Bart Simpson" required="" id="name" />'
    }

}