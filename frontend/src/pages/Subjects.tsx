import { PencilSimple, Trash } from "phosphor-react";
import Header from "../components/header";
import { useEffect, useState } from "react";
import SubjectForm from "../components/subjectForm";
import { useAppContext } from "../contexts";
import useUpdateSubject from "../hooks/subjects/useUpdateSubject";
import useCreateSubject from "../hooks/subjects/useCreateSubject";
import useDeleteSubject from "../hooks/subjects/useDeleteSubject";
import type { Subject } from "../types/subject";
import PageWrapper from "../components/pageWrapper";
import ContentWrapper from "../components/contentWrapper";
import BackButton from "../components/backButton";
import Heading from "../components/heading";
import Toast, { type ToastConfig } from "../components/toast";

const SubjectsPage = () => {
  const { subjects, selectedSubjectId, setSelectedSubjectId, setSubjects } =
    useAppContext();

  const [editSubjectId, setEditSubjectId] = useState<number | null>(null);
  const [toastConfig, setToastConfig] = useState<ToastConfig>(null);

  const { updateSubject } = useUpdateSubject();
  const { createSubject } = useCreateSubject();
  const { deleteSubject } = useDeleteSubject();

  const handleSelectSubject = (id: number) => {
    setSelectedSubjectId(id);
  };

  useEffect(() => {
    if (subjects.length > 0 && !selectedSubjectId) {
      setSelectedSubjectId(subjects[0].id);
    }
  }, [subjects, selectedSubjectId, setSelectedSubjectId]);

  const handleSaveEdit = async (
    id: number,
    updated: { name: string; frontLabel: string; backLabel: string }
  ) => {
    try {
      await updateSubject({
        id,
        name: updated.name,
        frontLabel: updated.frontLabel,
        backLabel: updated.backLabel,
      });

      setSubjects((prev) =>
        prev.map((s) => (s.id === id ? { ...s, ...updated } : s))
      );

      setEditSubjectId(null);
    } catch (err) {
      console.error("Failed to update subject:", err);
    }
  };

  const handleAddSubject = async (newSub: {
    name: string;
    frontLabel: string;
    backLabel: string;
  }) => {
    try {
      const created = await createSubject(newSub);
      if (created && created.id) {
        setSubjects((prev) => [...prev, created]);
        setSelectedSubjectId(created.id);
      }
    } catch (err) {
      console.error("Failed to create subject:", err);
    }
  };

  const confirmDeleteSubject = (subject: Subject) => {
    setToastConfig({
      message: `Are you sure you want to delete "${subject.name}"?`,
      isError: true,
      confirm: {
        onConfirm: async () => {
          try {
            const success = await deleteSubject(subject.id);
            if (success) {
              setSubjects((prev) => prev.filter((s) => s.id !== subject.id));

              setSelectedSubjectId((prevSelected) =>
                prevSelected === subject.id && subjects.length > 1
                  ? subjects.find((s) => s.id !== subject.id)?.id || null
                  : prevSelected === subject.id
                  ? null
                  : prevSelected
              );
            }
          } catch (err) {
            console.error("Failed to delete subject:", err);
          }
        },
        onCancel: () => {},
      },
    });
  };

  return (
    <PageWrapper className="bg-yellow-200 min-h-screen">
      <Header />
      <ContentWrapper>
        <div className="flex items-center mb-6">
          {subjects.length > 0 && <BackButton />}
          <Heading>Subjects</Heading>
        </div>

        {subjects.length === 0 && (
          <p className="text-center">Create a subject to begin studying</p>
        )}

        <ul className="space-y-3">
          {subjects.map((subject) => {
            const isActive = subject.id === selectedSubjectId;
            const isEditing = editSubjectId === subject.id;

            return (
              <li
                key={subject.id}
                className="flex flex-col border-2 p-3 rounded cursor-pointer hover:bg-gray-50"
                onClick={() => !isEditing && handleSelectSubject(subject.id)}
              >
                {isEditing ? (
                  <SubjectForm
                    mode="edit"
                    subject={subject}
                    onSave={(values) => handleSaveEdit(subject.id, values)}
                    onCancel={() => setEditSubjectId(null)}
                  />
                ) : (
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-5 h-5 rounded-full border-2 flex items-center justify-center">
                        {isActive && (
                          <div className="w-3 h-3 bg-black rounded-full"></div>
                        )}
                      </div>
                      <div>
                        <p className="font-semibold">{subject.name}</p>
                        <p className="text-xs text-gray-500">
                          {subject?.frontLabel || "Front"} |{" "}
                          {subject?.backLabel || "Back"}
                        </p>
                      </div>
                    </div>

                    <div className="flex flex-row gap-2">
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          setEditSubjectId(subject.id);
                        }}
                        className="p-2 border-inherit border-2 hover:border-black hover:bg-yellow-200 rounded hover:cursor-pointer"
                      >
                        <PencilSimple size={23} />
                      </button>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          confirmDeleteSubject(subject);
                        }}
                        className="p-2 border-inherit border-2 bg-black hover:border-black hover:bg-pink-600 rounded hover:cursor-pointer"
                      >
                        <Trash size={23} color="white" />
                      </button>
                    </div>
                  </div>
                )}
              </li>
            );
          })}
        </ul>

        <div className="mt-6 border-t-2 pt-4">
          <h2 className="text-lg font-bold mb-2">Add New Subject</h2>
          <SubjectForm mode="add" onSave={handleAddSubject} />
        </div>
      </ContentWrapper>

      {/* Toast */}
      {toastConfig && (
        <Toast
          message={toastConfig.message}
          isError={toastConfig.isError}
          confirm={toastConfig.confirm}
          onClose={() => setToastConfig(null)}
        />
      )}
    </PageWrapper>
  );
};

export default SubjectsPage;
